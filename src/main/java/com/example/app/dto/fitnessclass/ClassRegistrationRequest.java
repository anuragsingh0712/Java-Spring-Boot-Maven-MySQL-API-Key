package com.example.app.dto.fitnessclass;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRegistrationRequest {

  @NotNull(message = "fitnessClassId is required")
  private Long fitnessClassId;

  @NotNull(message = "memberId is required")
  private Long memberId;

  private String idempotencyKey;
}
