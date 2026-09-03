package com.example.app.dto.payment;

import com.example.app.entity.PaymentPurpose;
import com.example.app.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
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
public class PaymentResponse {

  private String id;
  private String memberId;
  private BigDecimal amount;
  private String currency;
  private PaymentPurpose purpose;
  private String referenceId;
  private PaymentStatus status;
  private String transactionRef;
  private BigDecimal refundedAmount;
  private Instant createdAt;
}
