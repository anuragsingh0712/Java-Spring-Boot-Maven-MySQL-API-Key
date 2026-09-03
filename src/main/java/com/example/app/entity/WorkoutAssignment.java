package com.example.app.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workout_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WorkoutAssignment extends BaseAuditEntity {

  @Id private String id;

  private String workoutProgramId;

  private String memberId;

  private LocalDate assignedDate;

  private WorkoutAssignmentStatus status;

  private String progressNotes;
}
