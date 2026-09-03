package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "branch")
public class Trainer extends BaseAuditEntity {

  @Id
  private String id;

  private String firstName;

  private String lastName;

  @Indexed(unique = true)
  private String email;

  private String phone;

  private String specialization;

  private String certifications;

  private Integer experienceYears;

  // Branch now lives in MongoDB (see Branch document); stored here as a plain
  // reference id since cross-store JPA relationships are not supported.
  private String branchId;

  private TrainerStatus status;
}
