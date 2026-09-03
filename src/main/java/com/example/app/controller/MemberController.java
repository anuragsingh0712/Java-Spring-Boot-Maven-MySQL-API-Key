package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.member.MemberRequest;
import com.example.app.dto.member.MemberResponse;
import com.example.app.service.MemberService;
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
@RequestMapping("/api/v1/members")
@Tag(name = "Members", description = "Member profile and lifecycle management endpoints")
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST')")
  @Operation(summary = "Create a member")
  public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request));
  }

  @GetMapping
  @Operation(summary = "List members (paginated)")
  public ResponseEntity<PageResponse<MemberResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(memberService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a member by id")
  public ResponseEntity<MemberResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(memberService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER','RECEPTIONIST')")
  @Operation(summary = "Update a member")
  public ResponseEntity<MemberResponse> update(
      @PathVariable Long id, @Valid @RequestBody MemberRequest request) {
    return ResponseEntity.ok(memberService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Delete a member")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    memberService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
