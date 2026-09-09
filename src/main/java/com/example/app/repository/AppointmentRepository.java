package com.example.app.repository;

import com.example.app.entity.Appointment;
import com.example.app.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

  @Query(
      "SELECT a FROM Appointment a WHERE a.trainerId = ?1 AND a.status IN ?4 "
          + "AND a.startTime < ?3 AND a.endTime > ?2")
  List<Appointment> findTrainerOverlaps(
      String trainerId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<AppointmentStatus> statuses);

  @Query(
      "SELECT a FROM Appointment a WHERE a.memberId = ?1 AND a.status IN ?4 "
          + "AND a.startTime < ?3 AND a.endTime > ?2")
  List<Appointment> findMemberOverlaps(
      String memberId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      List<AppointmentStatus> statuses);

  Page<Appointment> findByMemberId(String memberId, Pageable pageable);

  Optional<Appointment> findByIdempotencyKey(String idempotencyKey);
}
