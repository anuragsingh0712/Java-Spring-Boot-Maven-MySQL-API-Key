package com.example.app.dto.member;

import com.example.app.entity.MemberStatus;
import java.time.LocalDate;
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
public class MemberResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private LocalDate dateOfBirth;
  private String address;
  private String emergencyContactName;
  private String emergencyContactPhone;
  private Long branchId;
  private String branchName;
  private MemberStatus status;
}
