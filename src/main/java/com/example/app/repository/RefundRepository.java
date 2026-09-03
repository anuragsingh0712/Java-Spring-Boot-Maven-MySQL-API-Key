package com.example.app.repository;

import com.example.app.entity.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefundRepository extends MongoRepository<Refund, String> {}
