package com.example.app.dto.branch;

import com.example.app.entity.BranchStatus;
import java.time.LocalTime;
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
public class BranchResponse {

  private Long id;
  private Long gymId;
  private String gymName;
  private String name;
  private String address;
  private String city;
  private String state;
  private String country;
  private LocalTime openingTime;
  private LocalTime closingTime;
  private Integer capacity;
  private String facilities;
  private String managerName;
  private String managerContact;
  private BranchStatus status;
}
