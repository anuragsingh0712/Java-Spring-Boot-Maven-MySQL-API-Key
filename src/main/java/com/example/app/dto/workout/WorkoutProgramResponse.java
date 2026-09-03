package com.example.app.dto.workout;

import com.example.app.entity.WorkoutLevel;
import java.util.List;
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
public class WorkoutProgramResponse {

  private Long id;
  private String name;
  private String description;
  private WorkoutLevel level;
  private Long trainerId;
  private String trainerName;
  private List<ExerciseResponse> exercises;
}
