package com.example.app.service;

import com.example.app.dto.payment.RefundRequest;
import com.example.app.dto.payment.RefundResponse;
import com.example.app.entity.Payment;
import com.example.app.entity.PaymentStatus;
import com.example.app.entity.Refund;
import com.example.app.entity.RefundStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.PaymentRepository;
import com.example.app.repository.RefundRepository;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RefundService {

  private final RefundRepository refundRepository;
  private final PaymentRepository paymentRepository;

  public RefundService(RefundRepository refundRepository, PaymentRepository paymentRepository) {
    this.refundRepository = refundRepository;
    this.paymentRepository = paymentRepository;
  }

  public RefundResponse create(RefundRequest request) {
    Payment payment =
        paymentRepository
            .findById(request.getPaymentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Payment not found: " + request.getPaymentId()));

    if (payment.getStatus() != PaymentStatus.SUCCESS
        && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
      throw new BusinessRuleException(
          "Only successful payments can be refunded. Current status: " + payment.getStatus());
    }

    BigDecimal alreadyRefunded =
        payment.getRefundedAmount() != null ? payment.getRefundedAmount() : BigDecimal.ZERO;
    BigDecimal remaining = payment.getAmount().subtract(alreadyRefunded);
    if (request.getAmount().compareTo(remaining) > 0) {
      throw new BusinessRuleException(
          "Refund amount exceeds remaining refundable amount: " + remaining);
    }

    Refund refund =
        Refund.builder()
            .paymentId(payment.getId())
            .amount(request.getAmount())
            .reason(request.getReason())
            .status(RefundStatus.SUCCESS)
            .idempotencyKey(request.getIdempotencyKey())
            .build();
    Refund saved = refundRepository.save(refund);

    BigDecimal newRefundedTotal = alreadyRefunded.add(request.getAmount());
    payment.setRefundedAmount(newRefundedTotal);
    payment.setStatus(
        newRefundedTotal.compareTo(payment.getAmount()) >= 0
            ? PaymentStatus.REFUNDED
            : PaymentStatus.PARTIALLY_REFUNDED);
    paymentRepository.save(payment);

    return toResponse(saved);
  }

  public Page<RefundResponse> list(Pageable pageable) {
    return refundRepository.findAll(pageable).map(this::toResponse);
  }

  public RefundResponse get(String id) {
    return toResponse(
        refundRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + id)));
  }

  private RefundResponse toResponse(Refund refund) {
    return RefundResponse.builder()
        .id(refund.getId())
        .paymentId(refund.getPaymentId())
        .amount(refund.getAmount())
        .reason(refund.getReason())
        .status(refund.getStatus())
        .createdAt(refund.getCreatedAt())
        .build();
  }
}
