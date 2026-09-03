package com.example.app.dto.attendance;

import com.example.app.entity.AttendanceType;
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
public class AttendanceResponse {

  private Long id;
  private Long memberId;
  private Long branchId;
  private AttendanceType type;
  private Long referenceId;
  private Instant checkInTime;
  private Instant checkOutTime;
}
