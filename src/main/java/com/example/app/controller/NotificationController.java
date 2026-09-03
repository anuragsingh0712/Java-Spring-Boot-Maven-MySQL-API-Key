package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.notification.NotificationResponse;
import com.example.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification history endpoints")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  @Operation(summary = "List notifications (optionally filtered by memberId, paginated)")
  public ResponseEntity<PageResponse<NotificationResponse>> list(
      @RequestParam(required = false) Long memberId, Pageable pageable) {
    if (memberId != null) {
      return ResponseEntity.ok(
          PageResponse.of(notificationService.listByMember(memberId, pageable)));
    }
    return ResponseEntity.ok(PageResponse.of(notificationService.list(pageable)));
  }

  @PutMapping("/{id}/read")
  @Operation(summary = "Mark a notification as read")
  public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
    return ResponseEntity.ok(notificationService.markRead(id));
  }
}
