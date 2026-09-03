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
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  @Query(
      "select a from Appointment a where a.trainer.id = :trainerId and a.status in :statuses "
          + "and a.startTime < :endTime and a.endTime > :startTime")
  List<Appointment> findTrainerOverlaps(
      @Param("trainerId") Long trainerId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("statuses") List<AppointmentStatus> statuses);

  @Query(
      "select a from Appointment a where a.member.id = :memberId and a.status in :statuses "
          + "and a.startTime < :endTime and a.endTime > :startTime")
  List<Appointment> findMemberOverlaps(
      @Param("memberId") Long memberId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("statuses") List<AppointmentStatus> statuses);

  Page<Appointment> findByMemberId(Long memberId, Pageable pageable);

  Optional<Appointment> findByIdempotencyKey(String idempotencyKey);
}
