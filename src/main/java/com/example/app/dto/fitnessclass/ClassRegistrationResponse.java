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

  private Long id;
  private Long fitnessClassId;
  private String fitnessClassName;
  private Long memberId;
  private RegistrationStatus status;
  private Integer waitlistPosition;
  private Instant registeredAt;
}
