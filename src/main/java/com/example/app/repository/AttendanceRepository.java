package com.example.app.repository;

import com.example.app.entity.Attendance;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {

  Optional<Attendance> findByMemberIdAndCheckOutTimeIsNull(String memberId);

  Page<Attendance> findByMemberId(String memberId, Pageable pageable);

  Page<Attendance> findByBranchId(String branchId, Pageable pageable);
}
