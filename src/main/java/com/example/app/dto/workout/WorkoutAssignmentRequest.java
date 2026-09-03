package com.example.app.dto.workout;

import com.example.app.entity.WorkoutAssignmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutAssignmentRequest {

  @NotNull(message = "workoutProgramId is required")
  private Long workoutProgramId;

  @NotNull(message = "memberId is required")
  private Long memberId;

  private LocalDate assignedDate;

  private WorkoutAssignmentStatus status;

  private String progressNotes;
}
