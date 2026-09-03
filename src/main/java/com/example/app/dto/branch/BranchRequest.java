package com.example.app.dto.branch;

import com.example.app.entity.BranchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {

  @NotNull(message = "gymId is required")
  private Long gymId;

  @NotBlank(message = "name is required")
  private String name;

  private String address;
  private String city;
  private String state;
  private String country;
  private LocalTime openingTime;
  private LocalTime closingTime;

  @NotNull(message = "capacity is required")
  @Positive(message = "capacity must be positive")
  private Integer capacity;

  private String facilities;
  private String managerName;
  private String managerContact;

  @NotNull(message = "status is required")
  private BranchStatus status;
}
