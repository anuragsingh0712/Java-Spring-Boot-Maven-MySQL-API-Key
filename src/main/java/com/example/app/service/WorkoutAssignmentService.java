package com.example.app.service;

import com.example.app.dto.workout.WorkoutAssignmentRequest;
import com.example.app.dto.workout.WorkoutAssignmentResponse;
import com.example.app.entity.Member;
import com.example.app.entity.WorkoutAssignment;
import com.example.app.entity.WorkoutAssignmentStatus;
import com.example.app.entity.WorkoutProgram;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.WorkoutAssignmentRepository;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkoutAssignmentService {

  private final WorkoutAssignmentRepository workoutAssignmentRepository;
  private final WorkoutProgramService workoutProgramService;
  private final MemberService memberService;

  public WorkoutAssignmentService(
      WorkoutAssignmentRepository workoutAssignmentRepository,
      WorkoutProgramService workoutProgramService,
      MemberService memberService) {
    this.workoutAssignmentRepository = workoutAssignmentRepository;
    this.workoutProgramService = workoutProgramService;
    this.memberService = memberService;
  }

  @Transactional
  public WorkoutAssignmentResponse assign(WorkoutAssignmentRequest request) {
    WorkoutProgram program = workoutProgramService.findOrThrow(request.getWorkoutProgramId());
    Member member = memberService.findOrThrow(request.getMemberId());

    WorkoutAssignment assignment =
        WorkoutAssignment.builder()
            .workoutProgram(program)
            .member(member)
            .assignedDate(
                request.getAssignedDate() != null ? request.getAssignedDate() : LocalDate.now())
            .status(
                request.getStatus() != null
                    ? request.getStatus()
                    : WorkoutAssignmentStatus.ASSIGNED)
            .progressNotes(request.getProgressNotes())
            .build();
    return toResponse(workoutAssignmentRepository.save(assignment));
  }

  public Page<WorkoutAssignmentResponse> list(Pageable pageable) {
    return workoutAssignmentRepository.findAll(pageable).map(this::toResponse);
  }

  public Page<WorkoutAssignmentResponse> historyByMember(Long memberId, Pageable pageable) {
    return workoutAssignmentRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public WorkoutAssignmentResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public WorkoutAssignmentResponse updateProgress(
      Long id, WorkoutAssignmentStatus status, String progressNotes) {
    WorkoutAssignment assignment = findOrThrow(id);
    if (status != null) {
      assignment.setStatus(status);
    }
    if (progressNotes != null) {
      assignment.setProgressNotes(progressNotes);
    }
    return toResponse(workoutAssignmentRepository.save(assignment));
  }

  @Transactional
  public void delete(Long id) {
    workoutAssignmentRepository.delete(findOrThrow(id));
  }

  private WorkoutAssignment findOrThrow(Long id) {
    return workoutAssignmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Workout assignment not found: " + id));
  }

  private WorkoutAssignmentResponse toResponse(WorkoutAssignment assignment) {
    return WorkoutAssignmentResponse.builder()
        .id(assignment.getId())
        .workoutProgramId(assignment.getWorkoutProgram().getId())
        .workoutProgramName(assignment.getWorkoutProgram().getName())
        .memberId(assignment.getMember().getId())
        .assignedDate(assignment.getAssignedDate())
        .status(assignment.getStatus())
        .progressNotes(assignment.getProgressNotes())
        .build();
  }
}
