package com.example.app.dto.membership;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipUpgradeRequest {

  @NotNull(message = "newPlanId is required")
  private Long newPlanId;

  private String idempotencyKey;
}
