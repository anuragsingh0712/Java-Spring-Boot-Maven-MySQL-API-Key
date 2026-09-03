package com.example.app.dto.fitnessclass;

import com.example.app.entity.ClassStatus;
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
public class FitnessClassResponse {

  private Long id;
  private String name;
  private String classType;
  private Long branchId;
  private String branchName;
  private Long trainerId;
  private String trainerName;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer capacity;
  private long registeredCount;
  private long waitlistCount;
  private ClassStatus status;
}
