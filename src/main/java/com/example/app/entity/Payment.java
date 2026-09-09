package com.example.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String memberId;

  private BigDecimal amount;

  private String currency;

  @Enumerated(EnumType.STRING)
  private PaymentPurpose purpose;

  private String referenceId;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(unique = true)
  private String idempotencyKey;

  private String transactionRef;

  private BigDecimal refundedAmount;
}
