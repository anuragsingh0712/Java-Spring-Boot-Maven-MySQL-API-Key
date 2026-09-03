package com.example.app.repository;

import com.example.app.entity.ClassRegistration;
import com.example.app.entity.RegistrationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRegistrationRepository extends JpaRepository<ClassRegistration, Long> {

  Optional<ClassRegistration> findByFitnessClassIdAndMemberIdAndStatusIn(
      Long fitnessClassId, Long memberId, List<RegistrationStatus> statuses);

  long countByFitnessClassIdAndStatus(Long fitnessClassId, RegistrationStatus status);

  List<ClassRegistration> findByFitnessClassIdAndStatusOrderByWaitlistPositionAsc(
      Long fitnessClassId, RegistrationStatus status);

  Page<ClassRegistration> findByMemberId(Long memberId, Pageable pageable);

  Optional<ClassRegistration> findByIdempotencyKey(String idempotencyKey);
}
