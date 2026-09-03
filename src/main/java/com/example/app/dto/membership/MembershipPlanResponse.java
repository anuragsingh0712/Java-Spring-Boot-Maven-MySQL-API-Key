package com.example.app.dto.membership;

import com.example.app.entity.MembershipType;
import java.math.BigDecimal;
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
public class MembershipPlanResponse {

  private Long id;
  private String name;
  private MembershipType type;
  private Integer durationDays;
  private BigDecimal price;
  private String description;
  private Boolean active;
}
