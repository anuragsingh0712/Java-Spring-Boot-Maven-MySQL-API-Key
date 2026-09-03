package com.example.app.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member extends BaseAuditEntity {

  @Id
  private String id;

  private String firstName;

  private String lastName;

  @Indexed(unique = true)
  private String email;

  private String phone;

  private LocalDate dateOfBirth;

  private String address;

  private String emergencyContactName;

  private String emergencyContactPhone;

  // Branch now lives in MongoDB (see Branch document); stored here as a plain
  // reference id since cross-store JPA relationships are not supported.
  private String branchId;

  private MemberStatus status;
}
