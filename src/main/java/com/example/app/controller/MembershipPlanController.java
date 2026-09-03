package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.membership.MembershipPlanRequest;
import com.example.app.dto.membership.MembershipPlanResponse;
import com.example.app.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/membership-plans")
@Tag(name = "Membership Plans", description = "Membership plan catalog endpoints")
public class MembershipPlanController {

  private final MembershipPlanService membershipPlanService;

  public MembershipPlanController(MembershipPlanService membershipPlanService) {
    this.membershipPlanService = membershipPlanService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Create a membership plan")
  public ResponseEntity<MembershipPlanResponse> create(
      @Valid @RequestBody MembershipPlanRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(membershipPlanService.create(request));
  }

  @GetMapping
  @Operation(summary = "List membership plans (paginated)")
  public ResponseEntity<PageResponse<MembershipPlanResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(membershipPlanService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a membership plan by id")
  public ResponseEntity<MembershipPlanResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(membershipPlanService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Update a membership plan")
  public ResponseEntity<MembershipPlanResponse> update(
      @PathVariable String id, @Valid @RequestBody MembershipPlanRequest request) {
    return ResponseEntity.ok(membershipPlanService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Delete a membership plan")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    membershipPlanService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
