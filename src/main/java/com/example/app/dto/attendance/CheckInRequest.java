package com.example.app.dto.attendance;

import com.example.app.entity.AttendanceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  @NotNull(message = "branchId is required")
  private Long branchId;

  @NotNull(message = "type is required")
  private AttendanceType type;

  private Long referenceId;
}
