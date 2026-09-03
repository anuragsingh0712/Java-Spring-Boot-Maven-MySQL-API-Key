package com.example.app.dto.workout;

import com.example.app.entity.WorkoutAssignmentStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutAssignmentResponse {

  private String id;
  private String workoutProgramId;
  private String workoutProgramName;
  private String memberId;
  private LocalDate assignedDate;
  private WorkoutAssignmentStatus status;
  private String progressNotes;
}
