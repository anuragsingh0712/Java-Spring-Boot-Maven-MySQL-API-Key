package com.example.app.repository;

import com.example.app.entity.Payment;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
