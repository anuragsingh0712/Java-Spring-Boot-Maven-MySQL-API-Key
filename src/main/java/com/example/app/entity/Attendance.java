package com.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Attendance extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String memberId;

  // Branch reference stored as a plain id column (no JPA relationship mapping),
  // matching the pre-existing cross-aggregate reference style.
  private String branchId;

  @Enumerated(EnumType.STRING)
  private AttendanceType type;

  private String referenceId;

  private Instant checkInTime;

  private Instant checkOutTime;
}
