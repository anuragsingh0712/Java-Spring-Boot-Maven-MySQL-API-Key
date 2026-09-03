package com.example.app.dto.workout;

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
public class ExerciseResponse {

  private String id;
  private String name;
  private String category;
  private Integer sets;
  private Integer reps;
  private Integer durationSeconds;
  private Integer orderIndex;
}
