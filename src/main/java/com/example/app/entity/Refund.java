package com.example.app.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Refund extends BaseAuditEntity {

  @Id private String id;

  private String paymentId;

  private BigDecimal amount;

  private String reason;

  private RefundStatus status;

  private String idempotencyKey;
}
