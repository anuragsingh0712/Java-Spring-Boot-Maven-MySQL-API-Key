package com.example.app.service;

import com.example.app.dto.fitnessclass.ClassRegistrationRequest;
import com.example.app.dto.fitnessclass.ClassRegistrationResponse;
import com.example.app.entity.ClassRegistration;
import com.example.app.entity.ClassStatus;
import com.example.app.entity.FitnessClass;
import com.example.app.entity.Member;
import com.example.app.entity.NotificationType;
import com.example.app.entity.RegistrationStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ConflictException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.ClassRegistrationRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClassRegistrationService {

  private static final List<RegistrationStatus> ACTIVE_STATUSES =
      List.of(RegistrationStatus.REGISTERED, RegistrationStatus.WAITLISTED);

  private final ClassRegistrationRepository classRegistrationRepository;
  private final FitnessClassService fitnessClassService;
  private final MemberService memberService;
  private final NotificationService notificationService;

  public ClassRegistrationService(
      ClassRegistrationRepository classRegistrationRepository,
      FitnessClassService fitnessClassService,
      MemberService memberService,
      NotificationService notificationService) {
    this.classRegistrationRepository = classRegistrationRepository;
    this.fitnessClassService = fitnessClassService;
    this.memberService = memberService;
    this.notificationService = notificationService;
  }

  @Transactional
  public synchronized ClassRegistrationResponse register(ClassRegistrationRequest request) {
    if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
      var existing = classRegistrationRepository.findByIdempotencyKey(request.getIdempotencyKey());
      if (existing.isPresent()) {
        return toResponse(existing.get());
      }
    }

    FitnessClass fitnessClass = fitnessClassService.findOrThrow(request.getFitnessClassId());
    if (fitnessClass.getStatus() != ClassStatus.SCHEDULED) {
      throw new BusinessRuleException(
          "Cannot register for a class that is not scheduled: " + fitnessClass.getStatus());
    }

    // Only active members can register.
    Member member = memberService.getServiceEligibleMember(request.getMemberId());

    classRegistrationRepository
        .findByFitnessClassIdAndMemberIdAndStatusIn(
            fitnessClass.getId(), member.getId(), ACTIVE_STATUSES)
        .ifPresent(
            r -> {
              throw new ConflictException(
                  "Member is already registered/waitlisted for this class: " + r.getId());
            });

    long registeredCount =
        classRegistrationRepository.countByFitnessClassIdAndStatus(
            fitnessClass.getId(), RegistrationStatus.REGISTERED);

    ClassRegistration registration;
    if (registeredCount < fitnessClass.getCapacity()) {
      registration =
          ClassRegistration.builder()
              .fitnessClass(fitnessClass)
              .member(member)
              .status(RegistrationStatus.REGISTERED)
              .registeredAt(Instant.now())
              .idempotencyKey(request.getIdempotencyKey())
              .build();
      notificationService.notify(
          member,
          NotificationType.CLASS_REGISTRATION,
          "Registered for class " + fitnessClass.getName());
    } else {
      long waitlistCount =
          classRegistrationRepository.countByFitnessClassIdAndStatus(
              fitnessClass.getId(), RegistrationStatus.WAITLISTED);
      registration =
          ClassRegistration.builder()
              .fitnessClass(fitnessClass)
              .member(member)
              .status(RegistrationStatus.WAITLISTED)
              .waitlistPosition((int) waitlistCount + 1)
              .registeredAt(Instant.now())
              .idempotencyKey(request.getIdempotencyKey())
              .build();
      notificationService.notify(
          member,
          NotificationType.CLASS_REGISTRATION,
          "Class "
              + fitnessClass.getName()
              + " is full. Added to waitlist at position "
              + registration.getWaitlistPosition());
    }
    return toResponse(classRegistrationRepository.save(registration));
  }

  public Page<ClassRegistrationResponse> list(Pageable pageable) {
    return classRegistrationRepository.findAll(pageable).map(this::toResponse);
  }

  public Page<ClassRegistrationResponse> historyByMember(Long memberId, Pageable pageable) {
    return classRegistrationRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public ClassRegistrationResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public synchronized ClassRegistrationResponse cancel(Long id) {
    ClassRegistration registration = findOrThrow(id);
    if (registration.getStatus() == RegistrationStatus.CANCELLED) {
      throw new BusinessRuleException("Registration is already cancelled");
    }
    boolean wasRegistered = registration.getStatus() == RegistrationStatus.REGISTERED;
    registration.setStatus(RegistrationStatus.CANCELLED);
    classRegistrationRepository.save(registration);
    notificationService.notify(
        registration.getMember(),
        NotificationType.CLASS_REGISTRATION,
        "Registration cancelled for class " + registration.getFitnessClass().getName());

    if (wasRegistered) {
      promoteNextWaitlisted(registration.getFitnessClass());
    }
    return toResponse(registration);
  }

  private void promoteNextWaitlisted(FitnessClass fitnessClass) {
    List<ClassRegistration> waitlist =
        classRegistrationRepository.findByFitnessClassIdAndStatusOrderByWaitlistPositionAsc(
            fitnessClass.getId(), RegistrationStatus.WAITLISTED);
    if (!waitlist.isEmpty()) {
      ClassRegistration next = waitlist.get(0);
      next.setStatus(RegistrationStatus.REGISTERED);
      next.setWaitlistPosition(null);
      classRegistrationRepository.save(next);
      notificationService.notify(
          next.getMember(),
          NotificationType.CLASS_REGISTRATION,
          "You have been promoted from the waitlist for class " + fitnessClass.getName());
    }
  }

  private ClassRegistration findOrThrow(Long id) {
    return classRegistrationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Class registration not found: " + id));
  }

  private ClassRegistrationResponse toResponse(ClassRegistration registration) {
    return ClassRegistrationResponse.builder()
        .id(registration.getId())
        .fitnessClassId(registration.getFitnessClass().getId())
        .fitnessClassName(registration.getFitnessClass().getName())
        .memberId(registration.getMember().getId())
        .status(registration.getStatus())
        .waitlistPosition(registration.getWaitlistPosition())
        .registeredAt(registration.getRegisteredAt())
        .build();
  }
}
