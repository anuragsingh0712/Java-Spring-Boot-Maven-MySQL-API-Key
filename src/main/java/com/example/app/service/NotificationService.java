package com.example.app.service;

import com.example.app.dto.notification.NotificationResponse;
import com.example.app.entity.Member;
import com.example.app.entity.Notification;
import com.example.app.entity.NotificationType;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate messagingTemplate;

  public NotificationService(
      NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
    this.notificationRepository = notificationRepository;
    this.messagingTemplate = messagingTemplate;
  }

  public NotificationResponse notify(Member member, NotificationType type, String message) {
    Notification notification =
        Notification.builder()
            .memberId(member.getId())
            .type(type)
            .message(message)
            .isRead(false)
            .build();
    NotificationResponse response = toResponse(notificationRepository.save(notification));
    try {
      messagingTemplate.convertAndSend("/topic/notifications", response);
    } catch (Exception ignored) {
      // Websocket broadcast failure must never break the primary business transaction.
    }
    return response;
  }

  public Page<NotificationResponse> list(Pageable pageable) {
    return notificationRepository.findAll(pageable).map(this::toResponse);
  }

  public Page<NotificationResponse> listByMember(String memberId, Pageable pageable) {
    return notificationRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public NotificationResponse markRead(String id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
    notification.setIsRead(true);
    return toResponse(notificationRepository.save(notification));
  }

  private NotificationResponse toResponse(Notification notification) {
    return NotificationResponse.builder()
        .id(notification.getId())
        .memberId(notification.getMemberId())
        .type(notification.getType())
        .message(notification.getMessage())
        .isRead(notification.getIsRead())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
