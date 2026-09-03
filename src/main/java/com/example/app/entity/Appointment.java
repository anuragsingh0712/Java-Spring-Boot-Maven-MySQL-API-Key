package com.example.app.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Appointment extends BaseAuditEntity {

  @Id
  private String id;

  private String memberId;

  private String trainerId;

  // Branch now lives in MongoDB (see Branch document); stored here as a plain
  // reference id since cross-store JPA relationships are not supported.
  private String branchId;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private AppointmentStatus status;

  private String notes;

  private String idempotencyKey;
}
