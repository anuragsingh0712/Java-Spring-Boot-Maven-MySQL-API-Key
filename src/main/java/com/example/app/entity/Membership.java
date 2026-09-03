package com.example.app.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Membership extends BaseAuditEntity {

  @Id
  private String id;

  private String memberId;

  private String planId;

  private LocalDate startDate;

  private LocalDate endDate;

  private MembershipStatus status;

  private BigDecimal price;

  private LocalDate pauseStartDate;

  private LocalDate pauseEndDate;
}
