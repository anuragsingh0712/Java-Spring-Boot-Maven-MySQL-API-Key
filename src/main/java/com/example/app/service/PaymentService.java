package com.example.app.service;

import com.example.app.dto.payment.PaymentRequest;
import com.example.app.dto.payment.PaymentResponse;
import com.example.app.entity.Member;
import com.example.app.entity.NotificationType;
import com.example.app.entity.Payment;
import com.example.app.entity.PaymentPurpose;
import com.example.app.entity.PaymentStatus;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final MemberService memberService;
  private final NotificationService notificationService;

  public PaymentService(
      PaymentRepository paymentRepository,
      MemberService memberService,
      NotificationService notificationService) {
    this.paymentRepository = paymentRepository;
    this.memberService = memberService;
    this.notificationService = notificationService;
  }

  @Transactional
  public PaymentResponse processStandalonePayment(
      PaymentRequest request, String idempotencyHeader) {
    Member member = memberService.findOrThrow(request.getMemberId());
    String idempotencyKey = resolveIdempotencyKey(request.getIdempotencyKey(), idempotencyHeader);

    Payment payment =
        processPayment(
            member,
            request.getAmount(),
            request.getCurrency(),
            request.getPurpose(),
            request.getReferenceId(),
            idempotencyKey,
            Boolean.TRUE.equals(request.getSimulateFailure()));
    return toResponse(payment);
  }

  /**
   * Core, reusable, idempotent payment-processing routine used both by the standalone payment
   * endpoint and by orchestration flows such as membership purchase/renewal. Never activates a paid
   * service on failure.
   */
  @Transactional
  public Payment processPayment(
      Member member,
      BigDecimal amount,
      String currency,
      PaymentPurpose purpose,
      Long referenceId,
      String idempotencyKey,
      boolean simulateFailure) {
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
      if (existing.isPresent()) {
        return existing.get();
      }
    }

    Payment payment =
        Payment.builder()
            .member(member)
            .amount(amount)
            .currency(currency)
            .purpose(purpose)
            .referenceId(referenceId)
            .status(PaymentStatus.INITIATED)
            .idempotencyKey(idempotencyKey)
            .build();
    payment = paymentRepository.save(payment);

    boolean success = !simulateFailure && amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    if (success) {
      payment.setStatus(PaymentStatus.SUCCESS);
      payment.setTransactionRef("TXN-" + UUID.randomUUID());
      notificationService.notify(
          member,
          NotificationType.PAYMENT_SUCCESS,
          "Payment of " + amount + " " + currency + " for " + purpose + " was successful.");
    } else {
      payment.setStatus(PaymentStatus.FAILED);
      notificationService.notify(
          member,
          NotificationType.PAYMENT_FAILURE,
          "Payment of " + amount + " " + currency + " for " + purpose + " has failed.");
    }
    return paymentRepository.save(payment);
  }

  public Page<PaymentResponse> list(Pageable pageable) {
    return paymentRepository.findAll(pageable).map(this::toResponse);
  }

  public PaymentResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  public Payment findOrThrow(Long id) {
    return paymentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
  }

  private String resolveIdempotencyKey(String bodyKey, String headerKey) {
    if (bodyKey != null && !bodyKey.isBlank()) {
      return bodyKey;
    }
    return headerKey;
  }

  PaymentResponse toResponse(Payment payment) {
    return PaymentResponse.builder()
        .id(payment.getId())
        .memberId(payment.getMember().getId())
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .purpose(payment.getPurpose())
        .referenceId(payment.getReferenceId())
        .status(payment.getStatus())
        .transactionRef(payment.getTransactionRef())
        .refundedAmount(payment.getRefundedAmount())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
