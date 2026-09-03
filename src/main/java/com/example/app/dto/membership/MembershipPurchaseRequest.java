package com.example.app.dto.membership;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipPurchaseRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  @NotNull(message = "planId is required")
  private Long planId;

  private String idempotencyKey;

  /**
   * Test/demo hook only: forces the underlying payment to fail so the membership-remains-pending
   * path can be exercised end-to-end.
   */
  private Boolean simulateFailure;
}
