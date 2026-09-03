package com.example.app.dto.membership;

import com.example.app.entity.MembershipStatus;
import java.math.BigDecimal;
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
public class MembershipResponse {

  private Long id;
  private Long memberId;
  private Long planId;
  private String planName;
  private LocalDate startDate;
  private LocalDate endDate;
  private MembershipStatus status;
  private BigDecimal price;
  private LocalDate pauseStartDate;
  private LocalDate pauseEndDate;
}
