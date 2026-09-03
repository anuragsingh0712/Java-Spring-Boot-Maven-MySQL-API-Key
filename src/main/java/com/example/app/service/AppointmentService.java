package com.example.app.service;

import com.example.app.dto.appointment.AppointmentRequest;
import com.example.app.dto.appointment.AppointmentResponse;
import com.example.app.entity.Appointment;
import com.example.app.entity.AppointmentStatus;
import com.example.app.entity.Branch;
import com.example.app.entity.Member;
import com.example.app.entity.MembershipStatus;
import com.example.app.entity.NotificationType;
import com.example.app.entity.Trainer;
import com.example.app.entity.TrainerStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ConflictException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.AppointmentRepository;
import com.example.app.repository.MembershipRepository;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

  private static final List<AppointmentStatus> ACTIVE_STATUSES =
      List.of(AppointmentStatus.REQUESTED, AppointmentStatus.CONFIRMED);

  private final AppointmentRepository appointmentRepository;
  private final MembershipRepository membershipRepository;
  private final MemberService memberService;
  private final TrainerService trainerService;
  private final BranchService branchService;
  private final NotificationService notificationService;

  public AppointmentService(
      AppointmentRepository appointmentRepository,
      MembershipRepository membershipRepository,
      MemberService memberService,
      TrainerService trainerService,
      BranchService branchService,
      NotificationService notificationService) {
    this.appointmentRepository = appointmentRepository;
    this.membershipRepository = membershipRepository;
    this.memberService = memberService;
    this.trainerService = trainerService;
    this.branchService = branchService;
    this.notificationService = notificationService;
  }

  @Transactional
  public synchronized AppointmentResponse book(AppointmentRequest request) {
    if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
      var existing = appointmentRepository.findByIdempotencyKey(request.getIdempotencyKey());
      if (existing.isPresent()) {
        return toResponse(existing.get());
      }
    }
    if (!request.getEndTime().isAfter(request.getStartTime())) {
      throw new BusinessRuleException("endTime must be after startTime");
    }

    Member member = memberService.getServiceEligibleMember(request.getMemberId());

    boolean hasActiveMembership =
        !membershipRepository
            .findByMemberIdAndStatusIn(member.getId(), List.of(MembershipStatus.ACTIVE))
            .isEmpty();
    if (!hasActiveMembership) {
      throw new BusinessRuleException(
          "Member must have an active membership to book a personal training appointment");
    }

    Trainer trainer = trainerService.findOrThrow(request.getTrainerId());
    if (trainer.getStatus() != TrainerStatus.ACTIVE) {
      throw new BusinessRuleException(
          "Trainer is not active and cannot be booked: " + trainer.getId());
    }

    Branch branch = branchService.findOrThrow(request.getBranchId());
    validateBranchOperatingHours(branch, request);

    List<Appointment> trainerConflicts =
        appointmentRepository.findTrainerOverlaps(
            trainer.getId(), request.getStartTime(), request.getEndTime(), ACTIVE_STATUSES);
    if (!trainerConflicts.isEmpty()) {
      throw new ConflictException("Trainer already has an appointment during this time slot");
    }

    List<Appointment> memberConflicts =
        appointmentRepository.findMemberOverlaps(
            member.getId(), request.getStartTime(), request.getEndTime(), ACTIVE_STATUSES);
    if (!memberConflicts.isEmpty()) {
      throw new ConflictException("Member already has an appointment during this time slot");
    }

    Appointment appointment =
        Appointment.builder()
            .member(member)
            .trainer(trainer)
            .branch(branch)
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .status(AppointmentStatus.REQUESTED)
            .notes(request.getNotes())
            .idempotencyKey(request.getIdempotencyKey())
            .build();
    Appointment saved = appointmentRepository.save(appointment);
    notificationService.notify(
        member,
        NotificationType.APPOINTMENT_CONFIRMATION,
        "Appointment requested with trainer "
            + trainer.getFirstName()
            + " on "
            + request.getStartTime());
    return toResponse(saved);
  }

  public Page<AppointmentResponse> list(Pageable pageable) {
    return appointmentRepository.findAll(pageable).map(this::toResponse);
  }

  public Page<AppointmentResponse> historyByMember(Long memberId, Pageable pageable) {
    return appointmentRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public AppointmentResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public AppointmentResponse confirm(Long id) {
    Appointment appointment = findOrThrow(id);
    if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
      throw new BusinessRuleException(
          "Only REQUESTED appointments can be confirmed. Current status: "
              + appointment.getStatus());
    }
    appointment.setStatus(AppointmentStatus.CONFIRMED);
    return toResponse(appointmentRepository.save(appointment));
  }

  @Transactional
  public AppointmentResponse cancel(Long id) {
    Appointment appointment = findOrThrow(id);
    if (appointment.getStatus() == AppointmentStatus.COMPLETED
        || appointment.getStatus() == AppointmentStatus.CANCELLED) {
      throw new BusinessRuleException(
          "Completed/cancelled appointments cannot be cancelled again: " + appointment.getStatus());
    }
    appointment.setStatus(AppointmentStatus.CANCELLED);
    Appointment saved = appointmentRepository.save(appointment);
    notificationService.notify(
        appointment.getMember(),
        NotificationType.APPOINTMENT_CANCELLATION,
        "Appointment cancelled with trainer " + appointment.getTrainer().getFirstName());
    return toResponse(saved);
  }

  @Transactional
  public AppointmentResponse complete(Long id) {
    Appointment appointment = findOrThrow(id);
    if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
      throw new BusinessRuleException(
          "Only CONFIRMED appointments can be completed. Current status: "
              + appointment.getStatus());
    }
    appointment.setStatus(AppointmentStatus.COMPLETED);
    return toResponse(appointmentRepository.save(appointment));
  }

  @Transactional
  public AppointmentResponse markNoShow(Long id) {
    Appointment appointment = findOrThrow(id);
    if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
      throw new BusinessRuleException(
          "Only CONFIRMED appointments can be marked as no-show. Current status: "
              + appointment.getStatus());
    }
    appointment.setStatus(AppointmentStatus.NO_SHOW);
    return toResponse(appointmentRepository.save(appointment));
  }

  private void validateBranchOperatingHours(Branch branch, AppointmentRequest request) {
    if (branch.getOpeningTime() == null || branch.getClosingTime() == null) {
      return;
    }
    LocalTime start = request.getStartTime().toLocalTime();
    LocalTime end = request.getEndTime().toLocalTime();
    if (start.isBefore(branch.getOpeningTime()) || end.isAfter(branch.getClosingTime())) {
      throw new BusinessRuleException(
          "Appointment time is outside branch operating hours ("
              + branch.getOpeningTime()
              + " - "
              + branch.getClosingTime()
              + ")");
    }
  }

  private Appointment findOrThrow(Long id) {
    return appointmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
  }

  private AppointmentResponse toResponse(Appointment appointment) {
    return AppointmentResponse.builder()
        .id(appointment.getId())
        .memberId(appointment.getMember().getId())
        .trainerId(appointment.getTrainer().getId())
        .trainerName(
            appointment.getTrainer().getFirstName() + " " + appointment.getTrainer().getLastName())
        .branchId(appointment.getBranch().getId())
        .startTime(appointment.getStartTime())
        .endTime(appointment.getEndTime())
        .status(appointment.getStatus())
        .notes(appointment.getNotes())
        .build();
  }
}
