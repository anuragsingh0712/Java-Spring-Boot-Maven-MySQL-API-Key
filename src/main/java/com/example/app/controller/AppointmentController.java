package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.appointment.AppointmentRequest;
import com.example.app.dto.appointment.AppointmentResponse;
import com.example.app.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Personal training appointment booking endpoints")
public class AppointmentController {

  private final AppointmentService appointmentService;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Book a personal training appointment (idempotent via Idempotency-Key)")
  public ResponseEntity<AppointmentResponse> book(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
      @Valid @RequestBody AppointmentRequest request) {
    if (request.getIdempotencyKey() == null) {
      request.setIdempotencyKey(idempotencyKeyHeader);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(request));
  }

  @GetMapping
  @Operation(summary = "List appointments (paginated)")
  public ResponseEntity<PageResponse<AppointmentResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(appointmentService.list(pageable)));
  }

  @GetMapping("/member/{memberId}")
  @Operation(summary = "View a member's appointment history (paginated)")
  public ResponseEntity<PageResponse<AppointmentResponse>> historyByMember(
      @PathVariable Long memberId, Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.of(appointmentService.historyByMember(memberId, pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an appointment by id")
  public ResponseEntity<AppointmentResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.get(id));
  }

  @PostMapping("/{id}/confirm")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','TRAINER')")
  @Operation(summary = "Confirm a requested appointment")
  public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.confirm(id));
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize(
      "hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','TRAINER','MEMBER')")
  @Operation(summary = "Cancel an appointment")
  public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.cancel(id));
  }

  @PostMapping("/{id}/complete")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Mark an appointment as completed")
  public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.complete(id));
  }

  @PostMapping("/{id}/no-show")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','TRAINER')")
  @Operation(summary = "Mark an appointment as a no-show")
  public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.markNoShow(id));
  }
}
