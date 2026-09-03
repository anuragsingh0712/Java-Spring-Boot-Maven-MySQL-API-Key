package com.example.app.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Attendance extends BaseAuditEntity {

  @Id
  private String id;

  private String memberId;

  // Branch now lives in MongoDB (see Branch document); stored here as a plain
  // reference id since cross-store JPA relationships are not supported.
  private String branchId;

  private AttendanceType type;

  private String referenceId;

  private Instant checkInTime;

  private Instant checkOutTime;
}
