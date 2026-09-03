package com.example.app.repository;

import com.example.app.entity.WorkoutAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutAssignmentRepository extends JpaRepository<WorkoutAssignment, Long> {

  Page<WorkoutAssignment> findByMemberId(Long memberId, Pageable pageable);
}
