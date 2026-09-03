package com.example.app.dto.membership;

import com.example.app.entity.MembershipType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipPlanRequest {

  @NotBlank(message = "name is required")
  private String name;

  @NotNull(message = "type is required")
  private MembershipType type;

  @NotNull(message = "durationDays is required")
  @Positive(message = "durationDays must be positive")
  private Integer durationDays;

  @NotNull(message = "price is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "price must not be negative")
  private BigDecimal price;

  private String description;

  private Boolean active;
}
