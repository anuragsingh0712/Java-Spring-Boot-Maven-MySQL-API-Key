package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gyms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Gym extends BaseAuditEntity {

  @Id
  private String id;

  private String name;

  @Indexed(unique = true, sparse = true)
  private String registrationNumber;

  private String contactEmail;

  private String contactPhone;

  private String description;
}
