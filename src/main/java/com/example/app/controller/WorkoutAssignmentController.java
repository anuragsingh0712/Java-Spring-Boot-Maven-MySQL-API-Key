package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.workout.WorkoutAssignmentRequest;
import com.example.app.dto.workout.WorkoutAssignmentResponse;
import com.example.app.entity.WorkoutAssignmentStatus;
import com.example.app.service.WorkoutAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout-assignments")
@Tag(
    name = "Workout Assignments",
    description = "Assign workout programs to members and track progress")
public class WorkoutAssignmentController {

  private final WorkoutAssignmentService workoutAssignmentService;

  public WorkoutAssignmentController(WorkoutAssignmentService workoutAssignmentService) {
    this.workoutAssignmentService = workoutAssignmentService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Assign a workout program to a member")
  public ResponseEntity<WorkoutAssignmentResponse> assign(
      @Valid @RequestBody WorkoutAssignmentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(workoutAssignmentService.assign(request));
  }

  @GetMapping
  @Operation(summary = "List workout assignments (paginated)")
  public ResponseEntity<PageResponse<WorkoutAssignmentResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(workoutAssignmentService.list(pageable)));
  }

  @GetMapping("/member/{memberId}")
  @Operation(summary = "View a member's workout assignment history (paginated)")
  public ResponseEntity<PageResponse<WorkoutAssignmentResponse>> historyByMember(
      @PathVariable Long memberId, Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.of(workoutAssignmentService.historyByMember(memberId, pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a workout assignment by id")
  public ResponseEntity<WorkoutAssignmentResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(workoutAssignmentService.get(id));
  }

  @PutMapping("/{id}/progress")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER','MEMBER')")
  @Operation(summary = "Update workout assignment status/progress notes")
  public ResponseEntity<WorkoutAssignmentResponse> updateProgress(
      @PathVariable Long id,
      @RequestParam(required = false) WorkoutAssignmentStatus status,
      @RequestParam(required = false) String progressNotes) {
    return ResponseEntity.ok(workoutAssignmentService.updateProgress(id, status, progressNotes));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Delete a workout assignment")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    workoutAssignmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
