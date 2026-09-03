package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.membership.MembershipPurchaseRequest;
import com.example.app.dto.membership.MembershipResponse;
import com.example.app.dto.membership.MembershipUpgradeRequest;
import com.example.app.service.MembershipService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/memberships")
@Tag(name = "Memberships", description = "Membership purchase, renewal and lifecycle endpoints")
public class MembershipController {

  private final MembershipService membershipService;

  public MembershipController(MembershipService membershipService) {
    this.membershipService = membershipService;
  }

  @PostMapping("/purchase")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(
      summary =
          "Purchase a membership plan (creates PENDING membership, processes payment, activates on"
              + " success)")
  public ResponseEntity<MembershipResponse> purchase(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
      @Valid @RequestBody MembershipPurchaseRequest request) {
    if (request.getIdempotencyKey() == null) {
      request.setIdempotencyKey(idempotencyKeyHeader);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.purchase(request));
  }

  @GetMapping
  @Operation(summary = "List memberships (paginated)")
  public ResponseEntity<PageResponse<MembershipResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(membershipService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a membership by id")
  public ResponseEntity<MembershipResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.get(id));
  }

  @GetMapping("/member/{memberId}/history")
  @Operation(summary = "View a member's membership history (paginated)")
  public ResponseEntity<PageResponse<MembershipResponse>> history(
      @PathVariable Long memberId, Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(membershipService.history(memberId, pageable)));
  }

  @PostMapping("/{id}/renew")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Renew a membership (extends the period on successful payment)")
  public ResponseEntity<MembershipResponse> renew(
      @PathVariable Long id,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @RequestParam(required = false) Boolean simulateFailure) {
    return ResponseEntity.ok(membershipService.renew(id, idempotencyKey, simulateFailure));
  }

  @PostMapping("/{id}/activate")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST')")
  @Operation(summary = "Manually activate a PENDING membership")
  public ResponseEntity<MembershipResponse> activate(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.activate(id));
  }

  @PostMapping("/{id}/pause")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Pause/freeze an ACTIVE membership")
  public ResponseEntity<MembershipResponse> pause(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.pause(id));
  }

  @PostMapping("/{id}/resume")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Resume a PAUSED membership")
  public ResponseEntity<MembershipResponse> resume(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.resume(id));
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Cancel a membership")
  public ResponseEntity<MembershipResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.cancel(id));
  }

  @PostMapping("/{id}/upgrade")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST','MEMBER')")
  @Operation(summary = "Upgrade or downgrade an ACTIVE membership to a different plan")
  public ResponseEntity<MembershipResponse> upgrade(
      @PathVariable Long id, @Valid @RequestBody MembershipUpgradeRequest request) {
    return ResponseEntity.ok(membershipService.upgrade(id, request));
  }
}
