package com.example.app.dto.workout;

import com.example.app.entity.WorkoutLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutProgramRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String description;

  @NotNull(message = "level is required")
  private WorkoutLevel level;

  private Long trainerId;

  @NotEmpty(message = "at least one exercise is required")
  @Valid
  private List<ExerciseRequest> exercises;
}
