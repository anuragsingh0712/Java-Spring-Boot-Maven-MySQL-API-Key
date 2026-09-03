package com.example.app.dto.fitnessclass;

import com.example.app.entity.RegistrationStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRegistrationResponse {

  private String id;
  private String fitnessClassId;
  private String fitnessClassName;
  private String memberId;
  private RegistrationStatus status;
  private Integer waitlistPosition;
  private Instant registeredAt;
}
