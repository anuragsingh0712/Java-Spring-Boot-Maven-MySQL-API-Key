package com.example.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String firstName;

  private String lastName;

  @Column(unique = true)
  private String email;

  private String phone;

  private LocalDate dateOfBirth;

  private String address;

  private String emergencyContactName;

  private String emergencyContactPhone;

  // Branch reference stored as a plain id column (no JPA relationship mapping),
  // matching the pre-existing cross-aggregate reference style.
  private String branchId;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;
}
