package com.example.app.repository;

import com.example.app.entity.FitnessClass;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FitnessClassRepository extends MongoRepository<FitnessClass, String> {}
