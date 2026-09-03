package com.example.app.dto.member;

import com.example.app.entity.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {

  @NotBlank(message = "firstName is required")
  private String firstName;

  @NotBlank(message = "lastName is required")
  private String lastName;

  @NotBlank(message = "email is required")
  @Email(message = "email must be valid")
  private String email;

  private String phone;

  @Past(message = "dateOfBirth must be in the past")
  private LocalDate dateOfBirth;

  private String address;
  private String emergencyContactName;
  private String emergencyContactPhone;

  @NotNull(message = "branchId is required")
  private Long branchId;

  @NotNull(message = "status is required")
  private MemberStatus status;
}
