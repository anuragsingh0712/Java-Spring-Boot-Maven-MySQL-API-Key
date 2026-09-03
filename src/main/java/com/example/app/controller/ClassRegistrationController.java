package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.fitnessclass.ClassRegistrationRequest;
import com.example.app.dto.fitnessclass.ClassRegistrationResponse;
import com.example.app.service.ClassRegistrationService;
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
@RequestMapping("/api/v1/class-registrations")
@Tag(
    name = "Class Registrations",
    description = "Fitness class registration and waitlist endpoints")
public class ClassRegistrationController {

  private final ClassRegistrationService classRegistrationService;

  public ClassRegistrationController(ClassRegistrationService classRegistrationService) {
    this.classRegistrationService = classRegistrationService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(
      summary =
          "Register a member for a class (auto-waitlists when full, idempotent via"
              + " Idempotency-Key)")
  public ResponseEntity<ClassRegistrationResponse> register(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
      @Valid @RequestBody ClassRegistrationRequest request) {
    if (request.getIdempotencyKey() == null) {
      request.setIdempotencyKey(idempotencyKeyHeader);
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(classRegistrationService.register(request));
  }

  @GetMapping
  @Operation(summary = "List class registrations (paginated)")
  public ResponseEntity<PageResponse<ClassRegistrationResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(classRegistrationService.list(pageable)));
  }

  @GetMapping("/member/{memberId}")
  @Operation(summary = "View a member's class registration history (paginated)")
  public ResponseEntity<PageResponse<ClassRegistrationResponse>> historyByMember(
      @PathVariable Long memberId, Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.of(classRegistrationService.historyByMember(memberId, pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a class registration by id")
  public ResponseEntity<ClassRegistrationResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(classRegistrationService.get(id));
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(
      summary = "Cancel a class registration (promotes the next waitlisted member automatically)")
  public ResponseEntity<ClassRegistrationResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(classRegistrationService.cancel(id));
  }
}
