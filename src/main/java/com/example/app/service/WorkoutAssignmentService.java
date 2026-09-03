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

@Service
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

  public WorkoutAssignmentResponse assign(WorkoutAssignmentRequest request) {
    WorkoutProgram program = workoutProgramService.findOrThrow(request.getWorkoutProgramId());
    Member member = memberService.findOrThrow(request.getMemberId());

    WorkoutAssignment assignment =
        WorkoutAssignment.builder()
            .workoutProgramId(program.getId())
            .memberId(member.getId())
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

  public Page<WorkoutAssignmentResponse> historyByMember(String memberId, Pageable pageable) {
    return workoutAssignmentRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public WorkoutAssignmentResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public WorkoutAssignmentResponse updateProgress(
      String id, WorkoutAssignmentStatus status, String progressNotes) {
    WorkoutAssignment assignment = findOrThrow(id);
    if (status != null) {
      assignment.setStatus(status);
    }
    if (progressNotes != null) {
      assignment.setProgressNotes(progressNotes);
    }
    return toResponse(workoutAssignmentRepository.save(assignment));
  }

  public void delete(String id) {
    workoutAssignmentRepository.delete(findOrThrow(id));
  }

  private WorkoutAssignment findOrThrow(String id) {
    return workoutAssignmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Workout assignment not found: " + id));
  }

  private WorkoutAssignmentResponse toResponse(WorkoutAssignment assignment) {
    WorkoutProgram program = workoutProgramService.findOrThrow(assignment.getWorkoutProgramId());
    return WorkoutAssignmentResponse.builder()
        .id(assignment.getId())
        .workoutProgramId(assignment.getWorkoutProgramId())
        .workoutProgramName(program.getName())
        .memberId(assignment.getMemberId())
        .assignedDate(assignment.getAssignedDate())
        .status(assignment.getStatus())
        .progressNotes(assignment.getProgressNotes())
        .build();
  }
}
