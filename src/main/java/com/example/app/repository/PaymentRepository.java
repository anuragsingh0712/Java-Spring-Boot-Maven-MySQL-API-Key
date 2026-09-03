package com.example.app.repository;

import com.example.app.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
