package com.example.app.dto.workout;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String category;
  private Integer sets;
  private Integer reps;
  private Integer durationSeconds;
  private Integer orderIndex;
}
