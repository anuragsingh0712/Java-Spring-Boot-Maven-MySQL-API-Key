package com.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fitness_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FitnessClass extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  private String classType;

  // Branch reference stored as a plain id column (no JPA relationship mapping),
  // matching the pre-existing cross-aggregate reference style.
  private String branchId;

  private String trainerId;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private Integer capacity;

  @Enumerated(EnumType.STRING)
  private ClassStatus status;
}
