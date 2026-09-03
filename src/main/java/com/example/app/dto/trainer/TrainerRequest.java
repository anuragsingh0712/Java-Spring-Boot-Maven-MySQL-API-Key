package com.example.app.dto.trainer;

import com.example.app.entity.TrainerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerRequest {

  @NotBlank(message = "firstName is required")
  private String firstName;

  @NotBlank(message = "lastName is required")
  private String lastName;

  @NotBlank(message = "email is required")
  @Email(message = "email must be valid")
  private String email;

  private String phone;
  private String specialization;
  private String certifications;
  private Integer experienceYears;

  @NotNull(message = "branchId is required")
  private String branchId;

  @NotNull(message = "status is required")
  private TrainerStatus status;
}
