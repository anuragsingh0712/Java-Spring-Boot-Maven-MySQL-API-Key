package com.example.app.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MembershipPlan extends BaseAuditEntity {

  @Id
  private String id;

  private String name;

  private MembershipType type;

  private Integer durationDays;

  private BigDecimal price;

  private String description;

  private Boolean active;
}
