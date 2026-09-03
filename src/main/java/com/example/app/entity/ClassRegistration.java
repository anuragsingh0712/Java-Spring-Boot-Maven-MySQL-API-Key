package com.example.app.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "class_registrations")
@CompoundIndex(
    name = "uq_class_registration_class_member",
    def = "{'fitnessClassId': 1, 'memberId': 1}",
    unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClassRegistration extends BaseAuditEntity {

  @Id private String id;

  private String fitnessClassId;

  private String memberId;

  private RegistrationStatus status;

  private Instant registeredAt;

  private Integer waitlistPosition;

  private String idempotencyKey;
}
