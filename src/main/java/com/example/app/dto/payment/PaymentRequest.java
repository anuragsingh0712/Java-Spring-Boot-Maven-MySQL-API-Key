package com.example.app.dto.payment;

import com.example.app.entity.PaymentPurpose;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

  @NotNull(message = "memberId is required")
  private String memberId;

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank(message = "currency is required")
  private String currency;

  @NotNull(message = "purpose is required")
  private PaymentPurpose purpose;

  private String referenceId;

  /**
   * Optional idempotency key. If omitted, the value of the Idempotency-Key HTTP header is used
   * instead.
   */
  private String idempotencyKey;

  /**
   * Test/demo hook only: when true, the simulated payment gateway fails the payment instead of
   * succeeding. Never present in a real gateway integration.
   */
  private Boolean simulateFailure;
}
