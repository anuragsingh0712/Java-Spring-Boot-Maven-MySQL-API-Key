package com.example.app.dto.notification;

import com.example.app.entity.NotificationType;
import java.time.Instant;
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
public class NotificationResponse {

  private String id;
  private String memberId;
  private NotificationType type;
  private String message;
  private Boolean isRead;
  private Instant createdAt;
}
