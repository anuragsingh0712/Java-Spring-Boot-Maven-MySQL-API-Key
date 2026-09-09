package com.example.app.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Exercise {

  private String id;

  private String name;

  private String category;

  private Integer sets;

  private Integer reps;

  private Integer durationSeconds;

  private Integer orderIndex;
}
