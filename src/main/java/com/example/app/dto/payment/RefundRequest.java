package com.example.app.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequest {

  @NotNull(message = "paymentId is required")
  private Long paymentId;

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be greater than zero")
  private BigDecimal amount;

  private String reason;

  private String idempotencyKey;
}
