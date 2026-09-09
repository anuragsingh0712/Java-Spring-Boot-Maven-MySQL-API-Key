package com.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "branches", indexes = @Index(name = "idx_branches_gym_id", columnList = "gymId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Branch extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String gymId;

  private String name;

  private String address;

  private String city;

  private String state;

  private String country;

  private LocalTime openingTime;

  private LocalTime closingTime;

  private Integer capacity;

  private String facilities;

  private String managerName;

  private String managerContact;

  @Enumerated(EnumType.STRING)
  private BranchStatus status;
}
