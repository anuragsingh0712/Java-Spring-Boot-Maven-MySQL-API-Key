package com.example.app.dto.trainer;

import com.example.app.entity.TrainerStatus;
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
public class TrainerResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String specialization;
  private String certifications;
  private Integer experienceYears;
  private Long branchId;
  private String branchName;
  private TrainerStatus status;
}
