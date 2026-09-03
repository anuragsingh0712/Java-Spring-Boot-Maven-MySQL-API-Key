package com.example.app.dto.gym;

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
public class GymResponse {

  private String id;
  private String name;
  private String registrationNumber;
  private String contactEmail;
  private String contactPhone;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
}
