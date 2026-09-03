package com.example.app.repository;

import com.example.app.entity.WorkoutAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkoutAssignmentRepository extends MongoRepository<WorkoutAssignment, String> {

  Page<WorkoutAssignment> findByMemberId(String memberId, Pageable pageable);
}
