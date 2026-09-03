package com.example.app.entity;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Branch extends BaseAuditEntity {

  @Id
  private String id;

  @Indexed
  private String gymId;

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
