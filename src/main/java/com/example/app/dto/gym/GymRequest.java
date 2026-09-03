package com.example.app.dto.gym;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GymRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String registrationNumber;

  @Email(message = "contactEmail must be a valid email")
  private String contactEmail;

  private String contactPhone;

  private String description;
}
