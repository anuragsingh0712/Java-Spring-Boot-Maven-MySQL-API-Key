package com.example.app.service;

import com.example.app.dto.membership.MembershipPurchaseRequest;
import com.example.app.dto.membership.MembershipResponse;
import com.example.app.dto.membership.MembershipUpgradeRequest;
import com.example.app.entity.Member;
import com.example.app.entity.Membership;
import com.example.app.entity.MembershipPlan;
import com.example.app.entity.MembershipStatus;
import com.example.app.entity.NotificationType;
import com.example.app.entity.Payment;
import com.example.app.entity.PaymentPurpose;
import com.example.app.entity.PaymentStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ConflictException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.MembershipRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {

  private static final List<MembershipStatus> BLOCKING_STATUSES =
      List.of(MembershipStatus.PENDING, MembershipStatus.ACTIVE, MembershipStatus.PAUSED);

  private final MembershipRepository membershipRepository;
  private final MemberService memberService;
  private final MembershipPlanService membershipPlanService;
  private final PaymentService paymentService;
  private final NotificationService notificationService;

  public MembershipService(
      MembershipRepository membershipRepository,
      MemberService memberService,
      MembershipPlanService membershipPlanService,
      PaymentService paymentService,
      NotificationService notificationService) {
    this.membershipRepository = membershipRepository;
    this.memberService = memberService;
    this.membershipPlanService = membershipPlanService;
    this.paymentService = paymentService;
    this.notificationService = notificationService;
  }

  public MembershipResponse purchase(MembershipPurchaseRequest request) {
    Member member = memberService.findOrThrow(request.getMemberId());
    MembershipPlan plan = membershipPlanService.findOrThrow(request.getPlanId());

    List<Membership> existing =
        membershipRepository.findByMemberIdAndStatusIn(member.getId(), BLOCKING_STATUSES);
    if (!existing.isEmpty()) {
      throw new ConflictException(
          "Member already has a pending/active/paused membership: " + existing.get(0).getId());
    }

    Membership membership =
        Membership.builder()
            .memberId(member.getId())
            .planId(plan.getId())
            .status(MembershipStatus.PENDING)
            .price(plan.getPrice())
            .build();
    membership = membershipRepository.save(membership);

    Payment payment =
        paymentService.processPayment(
            member,
            plan.getPrice(),
            "USD",
            PaymentPurpose.MEMBERSHIP_PURCHASE,
            membership.getId(),
            request.getIdempotencyKey(),
            Boolean.TRUE.equals(request.getSimulateFailure()));

    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      membership.setStatus(MembershipStatus.ACTIVE);
      membership.setStartDate(LocalDate.now());
      membership.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
      notificationService.notify(
          member,
          NotificationType.MEMBERSHIP_RENEWAL,
          "Membership activated using plan " + plan.getName());
    }
    // If payment failed, membership remains PENDING - no inconsistent partial activation.
    return toResponse(membershipRepository.save(membership));
  }

  public Page<MembershipResponse> list(Pageable pageable) {
    return membershipRepository.findAll(pageable).map(this::toResponseWithLazyExpiry);
  }

  public Page<MembershipResponse> history(String memberId, Pageable pageable) {
    return membershipRepository
        .findByMemberId(memberId, pageable)
        .map(this::toResponseWithLazyExpiry);
  }

  public MembershipResponse get(String id) {
    return toResponseWithLazyExpiry(findOrThrow(id));
  }

  public MembershipResponse renew(String id, String idempotencyKey, Boolean simulateFailure) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() == MembershipStatus.CANCELLED) {
      throw new BusinessRuleException("Cancelled memberships cannot be renewed");
    }
    MembershipPlan plan = membershipPlanService.findOrThrow(membership.getPlanId());
    Member member = memberService.findOrThrow(membership.getMemberId());

    Payment payment =
        paymentService.processPayment(
            member,
            plan.getPrice(),
            "USD",
            PaymentPurpose.MEMBERSHIP_RENEWAL,
            membership.getId(),
            idempotencyKey,
            Boolean.TRUE.equals(simulateFailure));

    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      LocalDate base =
          membership.getEndDate() != null && membership.getEndDate().isAfter(LocalDate.now())
              ? membership.getEndDate()
              : LocalDate.now();
      membership.setEndDate(base.plusDays(plan.getDurationDays()));
      membership.setStatus(MembershipStatus.ACTIVE);
      if (membership.getStartDate() == null) {
        membership.setStartDate(LocalDate.now());
      }
      notificationService.notify(
          member,
          NotificationType.MEMBERSHIP_RENEWAL,
          "Membership renewed until " + membership.getEndDate());
    }
    return toResponse(membershipRepository.save(membership));
  }

  public MembershipResponse activate(String id) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() != MembershipStatus.PENDING) {
      throw new BusinessRuleException(
          "Only PENDING memberships can be activated. Current status: " + membership.getStatus());
    }
    MembershipPlan plan = membershipPlanService.findOrThrow(membership.getPlanId());
    membership.setStatus(MembershipStatus.ACTIVE);
    membership.setStartDate(LocalDate.now());
    membership.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
    return toResponse(membershipRepository.save(membership));
  }

  public MembershipResponse pause(String id) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() != MembershipStatus.ACTIVE) {
      throw new BusinessRuleException(
          "Only ACTIVE memberships can be paused. Current status: " + membership.getStatus());
    }
    membership.setStatus(MembershipStatus.PAUSED);
    membership.setPauseStartDate(LocalDate.now());
    return toResponse(membershipRepository.save(membership));
  }

  public MembershipResponse resume(String id) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() != MembershipStatus.PAUSED) {
      throw new BusinessRuleException(
          "Only PAUSED memberships can be resumed. Current status: " + membership.getStatus());
    }
    membership.setPauseEndDate(LocalDate.now());
    long pausedDays =
        ChronoUnit.DAYS.between(membership.getPauseStartDate(), membership.getPauseEndDate());
    if (membership.getEndDate() != null) {
      membership.setEndDate(membership.getEndDate().plusDays(Math.max(pausedDays, 0)));
    }
    membership.setStatus(MembershipStatus.ACTIVE);
    return toResponse(membershipRepository.save(membership));
  }

  public MembershipResponse cancel(String id) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() == MembershipStatus.CANCELLED) {
      throw new BusinessRuleException("Membership is already cancelled");
    }
    membership.setStatus(MembershipStatus.CANCELLED);
    return toResponse(membershipRepository.save(membership));
  }

  public MembershipResponse upgrade(String id, MembershipUpgradeRequest request) {
    Membership membership = findOrThrow(id);
    if (membership.getStatus() != MembershipStatus.ACTIVE) {
      throw new BusinessRuleException(
          "Only ACTIVE memberships can be upgraded/downgraded. Current status: "
              + membership.getStatus());
    }
    MembershipPlan newPlan = membershipPlanService.findOrThrow(request.getNewPlanId());
    Member member = memberService.findOrThrow(membership.getMemberId());

    Payment payment =
        paymentService.processPayment(
            member,
            newPlan.getPrice(),
            "USD",
            PaymentPurpose.MEMBERSHIP_PURCHASE,
            membership.getId(),
            request.getIdempotencyKey(),
            false);

    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      membership.setPlanId(newPlan.getId());
      membership.setPrice(newPlan.getPrice());
      membership.setEndDate(LocalDate.now().plusDays(newPlan.getDurationDays()));
    }
    return toResponse(membershipRepository.save(membership));
  }

  public Membership findOrThrow(String id) {
    return membershipRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + id));
  }

  private MembershipResponse toResponseWithLazyExpiry(Membership membership) {
    if (membership.getStatus() == MembershipStatus.ACTIVE
        && membership.getEndDate() != null
        && membership.getEndDate().isBefore(LocalDate.now())) {
      membership.setStatus(MembershipStatus.EXPIRED);
      membership = membershipRepository.save(membership);
    }
    return toResponse(membership);
  }

  private MembershipResponse toResponse(Membership membership) {
    MembershipPlan plan = membershipPlanService.findOrThrow(membership.getPlanId());
    return MembershipResponse.builder()
        .id(membership.getId())
        .memberId(membership.getMemberId())
        .planId(membership.getPlanId())
        .planName(plan.getName())
        .startDate(membership.getStartDate())
        .endDate(membership.getEndDate())
        .status(membership.getStatus())
        .price(membership.getPrice())
        .pauseStartDate(membership.getPauseStartDate())
        .pauseEndDate(membership.getPauseEndDate())
        .build();
  }
}
