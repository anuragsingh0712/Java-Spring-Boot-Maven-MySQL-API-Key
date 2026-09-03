package com.example.app.dto.fitnessclass;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FitnessClassRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String classType;

  @NotNull(message = "branchId is required")
  private String branchId;

  @NotNull(message = "trainerId is required")
  private String trainerId;

  @NotNull(message = "startTime is required")
  @FutureOrPresent(message = "startTime must not be in the past")
  private LocalDateTime startTime;

  @NotNull(message = "endTime is required")
  private LocalDateTime endTime;

  @NotNull(message = "capacity is required")
  @Positive(message = "capacity must be positive")
  private Integer capacity;
}
