package com.example.app.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment extends BaseAuditEntity {

  @Id private String id;

  private String memberId;

  private BigDecimal amount;

  private String currency;

  private PaymentPurpose purpose;

  private String referenceId;

  private PaymentStatus status;

  @Indexed(unique = true, sparse = true)
  private String idempotencyKey;

  private String transactionRef;

  private BigDecimal refundedAmount;
}
