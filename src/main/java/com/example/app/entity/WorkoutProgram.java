package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workout_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"exercises"})
public class WorkoutProgram extends BaseAuditEntity {

  @Id private String id;

  private String name;

  private String description;

  private WorkoutLevel level;

  private String trainerId;

  @Builder.Default private List<Exercise> exercises = new ArrayList<>();
}
