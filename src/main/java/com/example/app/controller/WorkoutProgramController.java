package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.workout.WorkoutProgramRequest;
import com.example.app.dto.workout.WorkoutProgramResponse;
import com.example.app.service.WorkoutProgramService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout-programs")
@Tag(name = "Workout Programs", description = "Workout program and exercise management endpoints")
public class WorkoutProgramController {

  private final WorkoutProgramService workoutProgramService;

  public WorkoutProgramController(WorkoutProgramService workoutProgramService) {
    this.workoutProgramService = workoutProgramService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Create a workout program with its exercises")
  public ResponseEntity<WorkoutProgramResponse> create(
      @Valid @RequestBody WorkoutProgramRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(workoutProgramService.create(request));
  }

  @GetMapping
  @Operation(summary = "List workout programs (paginated)")
  public ResponseEntity<PageResponse<WorkoutProgramResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(workoutProgramService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a workout program by id")
  public ResponseEntity<WorkoutProgramResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(workoutProgramService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Update a workout program")
  public ResponseEntity<WorkoutProgramResponse> update(
      @PathVariable Long id, @Valid @RequestBody WorkoutProgramRequest request) {
    return ResponseEntity.ok(workoutProgramService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Delete a workout program")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    workoutProgramService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
