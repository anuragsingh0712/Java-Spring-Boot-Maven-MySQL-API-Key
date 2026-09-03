package com.example.app.repository;

import com.example.app.entity.Attendance;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  Optional<Attendance> findByMemberIdAndCheckOutTimeIsNull(Long memberId);

  Page<Attendance> findByMemberId(Long memberId, Pageable pageable);

  Page<Attendance> findByBranchId(Long branchId, Pageable pageable);
}
