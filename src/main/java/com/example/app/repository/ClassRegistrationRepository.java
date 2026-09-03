package com.example.app.repository;

import com.example.app.entity.ClassRegistration;
import com.example.app.entity.RegistrationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassRegistrationRepository extends MongoRepository<ClassRegistration, String> {

  Optional<ClassRegistration> findByFitnessClassIdAndMemberIdAndStatusIn(
      String fitnessClassId, String memberId, List<RegistrationStatus> statuses);

  long countByFitnessClassIdAndStatus(String fitnessClassId, RegistrationStatus status);

  List<ClassRegistration> findByFitnessClassIdAndStatusOrderByWaitlistPositionAsc(
      String fitnessClassId, RegistrationStatus status);

  Page<ClassRegistration> findByMemberId(String memberId, Pageable pageable);

  Optional<ClassRegistration> findByIdempotencyKey(String idempotencyKey);
}
