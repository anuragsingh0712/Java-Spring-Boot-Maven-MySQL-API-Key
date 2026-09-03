package com.example.app.dto.payment;

import com.example.app.entity.RefundStatus;
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
public class RefundResponse {

  private Long id;
  private Long paymentId;
  private BigDecimal amount;
  private String reason;
  private RefundStatus status;
  private Instant createdAt;
}
