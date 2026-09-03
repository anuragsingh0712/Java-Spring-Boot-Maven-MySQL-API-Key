package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.fitnessclass.FitnessClassRequest;
import com.example.app.dto.fitnessclass.FitnessClassResponse;
import com.example.app.service.FitnessClassService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fitness-classes")
@Tag(name = "Fitness Classes", description = "Fitness class scheduling endpoints")
public class FitnessClassController {

  private final FitnessClassService fitnessClassService;

  public FitnessClassController(FitnessClassService fitnessClassService) {
    this.fitnessClassService = fitnessClassService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Schedule a fitness class")
  public ResponseEntity<FitnessClassResponse> create(
      @Valid @RequestBody FitnessClassRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(fitnessClassService.create(request));
  }

  @GetMapping
  @Operation(summary = "List fitness classes (paginated)")
  public ResponseEntity<PageResponse<FitnessClassResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(fitnessClassService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a fitness class by id")
  public ResponseEntity<FitnessClassResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(fitnessClassService.get(id));
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Cancel a scheduled fitness class")
  public ResponseEntity<FitnessClassResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(fitnessClassService.cancel(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Delete a fitness class")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    fitnessClassService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
