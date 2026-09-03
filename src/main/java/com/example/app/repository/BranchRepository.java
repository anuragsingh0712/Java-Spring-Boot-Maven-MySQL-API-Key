package com.example.app.repository;

import com.example.app.entity.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BranchRepository extends MongoRepository<Branch, String> {

  boolean existsByGymId(String gymId);
}
