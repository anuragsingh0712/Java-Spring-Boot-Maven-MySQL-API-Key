package com.example.app.repository;

import com.example.app.entity.Gym;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GymRepository extends MongoRepository<Gym, String> {}
