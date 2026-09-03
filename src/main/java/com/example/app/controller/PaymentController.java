package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.payment.PaymentRequest;
import com.example.app.dto.payment.PaymentResponse;
import com.example.app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment processing and history endpoints")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  @Operation(summary = "Process a payment (idempotent via Idempotency-Key header or body field)")
  public ResponseEntity<PaymentResponse> process(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody PaymentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(paymentService.processStandalonePayment(request, idempotencyKey));
  }

  @GetMapping
  @Operation(summary = "List payments (paginated)")
  public ResponseEntity<PageResponse<PaymentResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(paymentService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a payment by id")
  public ResponseEntity<PaymentResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(paymentService.get(id));
  }
}
