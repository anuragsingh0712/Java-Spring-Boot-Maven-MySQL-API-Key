package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.attendance.AttendanceResponse;
import com.example.app.dto.attendance.CheckInRequest;
import com.example.app.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(
    name = "Attendance",
    description = "Member check-in/check-out and attendance history endpoints")
public class AttendanceController {

  private final AttendanceService attendanceService;

  public AttendanceController(AttendanceService attendanceService) {
    this.attendanceService = attendanceService;
  }

  @PostMapping("/check-in")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(
      summary =
          "Check in a member (rejects blocked/suspended/expired members and duplicate active"
              + " check-ins)")
  public ResponseEntity<AttendanceResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn(request));
  }

  @PostMapping("/check-out/{memberId}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Check out a member (must correspond to an existing active check-in)")
  public ResponseEntity<AttendanceResponse> checkOut(@PathVariable Long memberId) {
    return ResponseEntity.ok(attendanceService.checkOut(memberId));
  }

  @GetMapping
  @Operation(summary = "List attendance records (paginated)")
  public ResponseEntity<PageResponse<AttendanceResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(attendanceService.list(pageable)));
  }

  @GetMapping("/member/{memberId}")
  @Operation(summary = "View a member's attendance history (paginated)")
  public ResponseEntity<PageResponse<AttendanceResponse>> historyByMember(
      @PathVariable Long memberId, Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.of(attendanceService.historyByMember(memberId, pageable)));
  }

  @GetMapping("/branch/{branchId}")
  @Operation(summary = "View a branch's attendance history (paginated)")
  public ResponseEntity<PageResponse<AttendanceResponse>> historyByBranch(
      @PathVariable Long branchId, Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.of(attendanceService.historyByBranch(branchId, pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an attendance record by id")
  public ResponseEntity<AttendanceResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(attendanceService.get(id));
  }
}
