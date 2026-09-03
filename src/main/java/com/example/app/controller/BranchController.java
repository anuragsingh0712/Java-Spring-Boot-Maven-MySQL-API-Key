package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.branch.BranchRequest;
import com.example.app.dto.branch.BranchResponse;
import com.example.app.service.BranchService;
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
@RequestMapping("/api/v1/branches")
@Tag(name = "Branches", description = "Gym branch management endpoints")
public class BranchController {

  private final BranchService branchService;

  public BranchController(BranchService branchService) {
    this.branchService = branchService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Create a branch")
  public ResponseEntity<BranchResponse> create(@Valid @RequestBody BranchRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(request));
  }

  @GetMapping
  @Operation(summary = "List branches (paginated)")
  public ResponseEntity<PageResponse<BranchResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(branchService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a branch by id")
  public ResponseEntity<BranchResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(branchService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Update a branch")
  public ResponseEntity<BranchResponse> update(
      @PathVariable Long id, @Valid @RequestBody BranchRequest request) {
    return ResponseEntity.ok(branchService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Delete a branch")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    branchService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
