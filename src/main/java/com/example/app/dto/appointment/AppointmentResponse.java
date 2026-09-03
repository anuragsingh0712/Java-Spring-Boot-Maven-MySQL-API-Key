package com.example.app.dto.appointment;

import com.example.app.entity.AppointmentStatus;
import java.time.LocalDateTime;
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
public class AppointmentResponse {

  private Long id;
  private Long memberId;
  private Long trainerId;
  private String trainerName;
  private Long branchId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private AppointmentStatus status;
  private String notes;
}
