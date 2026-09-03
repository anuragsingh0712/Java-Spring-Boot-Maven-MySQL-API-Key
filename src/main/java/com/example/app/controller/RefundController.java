package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.payment.RefundRequest;
import com.example.app.dto.payment.RefundResponse;
import com.example.app.service.RefundService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/refunds")
@Tag(name = "Refunds", description = "Refund processing endpoints")
public class RefundController {

  private final RefundService refundService;

  public RefundController(RefundService refundService) {
    this.refundService = refundService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST')")
  @Operation(summary = "Create a refund against a successful payment")
  public ResponseEntity<RefundResponse> create(@Valid @RequestBody RefundRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(refundService.create(request));
  }

  @GetMapping
  @Operation(summary = "List refunds (paginated)")
  public ResponseEntity<PageResponse<RefundResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(refundService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a refund by id")
  public ResponseEntity<RefundResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(refundService.get(id));
  }
}
