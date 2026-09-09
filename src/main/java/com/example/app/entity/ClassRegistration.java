package com.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "class_registrations",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_class_registration_class_member",
            columnNames = {"fitnessClassId", "memberId"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClassRegistration extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String fitnessClassId;

  private String memberId;

  @Enumerated(EnumType.STRING)
  private RegistrationStatus status;

  private Instant registeredAt;

  private Integer waitlistPosition;

  private String idempotencyKey;
}
