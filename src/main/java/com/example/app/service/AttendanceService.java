package com.example.app.service;

import com.example.app.dto.attendance.AttendanceResponse;
import com.example.app.dto.attendance.CheckInRequest;
import com.example.app.entity.Attendance;
import com.example.app.entity.Branch;
import com.example.app.entity.Member;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.AttendanceRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final MemberService memberService;
  private final BranchService branchService;

  public AttendanceService(
      AttendanceRepository attendanceRepository,
      MemberService memberService,
      BranchService branchService) {
    this.attendanceRepository = attendanceRepository;
    this.memberService = memberService;
    this.branchService = branchService;
  }

  public synchronized AttendanceResponse checkIn(CheckInRequest request) {
    // Only active members can check in; blocked/suspended/expired members are rejected.
    Member member = memberService.getServiceEligibleMember(request.getMemberId());
    Branch branch = branchService.findOrThrow(request.getBranchId());

    attendanceRepository
        .findByMemberIdAndCheckOutTimeIsNull(member.getId())
        .ifPresent(
            a -> {
              throw new BusinessRuleException(
                  "Member already has an active check-in: " + a.getId());
            });

    Attendance attendance =
        Attendance.builder()
            .memberId(member.getId())
            .branchId(branch.getId())
            .type(request.getType())
            .referenceId(request.getReferenceId())
            .checkInTime(Instant.now())
            .build();
    return toResponse(attendanceRepository.save(attendance));
  }

  public synchronized AttendanceResponse checkOut(String memberId) {
    Attendance attendance =
        attendanceRepository
            .findByMemberIdAndCheckOutTimeIsNull(memberId)
            .orElseThrow(
                () ->
                    new BusinessRuleException("No active check-in found for member: " + memberId));
    attendance.setCheckOutTime(Instant.now());
    return toResponse(attendanceRepository.save(attendance));
  }

  public Page<AttendanceResponse> list(Pageable pageable) {
    return attendanceRepository.findAll(pageable).map(this::toResponse);
  }

  public Page<AttendanceResponse> historyByMember(String memberId, Pageable pageable) {
    return attendanceRepository.findByMemberId(memberId, pageable).map(this::toResponse);
  }

  public Page<AttendanceResponse> historyByBranch(String branchId, Pageable pageable) {
    return attendanceRepository.findByBranchId(branchId, pageable).map(this::toResponse);
  }

  public AttendanceResponse get(String id) {
    return toResponse(
        attendanceRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Attendance record not found: " + id)));
  }

  private AttendanceResponse toResponse(Attendance attendance) {
    return AttendanceResponse.builder()
        .id(attendance.getId())
        .memberId(attendance.getMemberId())
        .branchId(attendance.getBranchId())
        .type(attendance.getType())
        .referenceId(attendance.getReferenceId())
        .checkInTime(attendance.getCheckInTime())
        .checkOutTime(attendance.getCheckOutTime())
        .build();
  }
}
