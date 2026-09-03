package com.example.app.dto.fitnessclass;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRegistrationRequest {

  @NotNull(message = "fitnessClassId is required")
  private String fitnessClassId;

  @NotNull(message = "memberId is required")
  private String memberId;

  private String idempotencyKey;
}
