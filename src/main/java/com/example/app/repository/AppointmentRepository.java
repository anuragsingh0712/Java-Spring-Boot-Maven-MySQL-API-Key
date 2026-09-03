package com.example.app.repository;

import com.example.app.entity.Appointment;
import com.example.app.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

  @Query(
      "{ 'trainerId': ?0, 'status': { '$in': ?3 }, 'startTime': { '$lt': ?2 }, 'endTime': { '$gt': ?1 } }")
  List<Appointment> findTrainerOverlaps(
      String trainerId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<AppointmentStatus> statuses);

  @Query(
      "{ 'memberId': ?0, 'status': { '$in': ?3 }, 'startTime': { '$lt': ?2 }, 'endTime': { '$gt': ?1 } }")
  List<Appointment> findMemberOverlaps(
      String memberId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<AppointmentStatus> statuses);

  Page<Appointment> findByMemberId(String memberId, Pageable pageable);

  Optional<Appointment> findByIdempotencyKey(String idempotencyKey);
}
