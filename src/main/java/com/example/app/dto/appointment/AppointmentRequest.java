package com.example.app.dto.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRequest {

  @NotNull(message = "memberId is required")
  private String memberId;

  @NotNull(message = "trainerId is required")
  private String trainerId;

  @NotNull(message = "branchId is required")
  private String branchId;

  @NotNull(message = "startTime is required")
  @FutureOrPresent(message = "startTime must not be in the past")
  private LocalDateTime startTime;

  @NotNull(message = "endTime is required")
  private LocalDateTime endTime;

  private String notes;

  private String idempotencyKey;
}
