package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.trainer.TrainerRequest;
import com.example.app.dto.trainer.TrainerResponse;
import com.example.app.service.TrainerService;
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
@RequestMapping("/api/v1/trainers")
@Tag(name = "Trainers", description = "Trainer management endpoints")
public class TrainerController {

  private final TrainerService trainerService;

  public TrainerController(TrainerService trainerService) {
    this.trainerService = trainerService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Create a trainer")
  public ResponseEntity<TrainerResponse> create(@Valid @RequestBody TrainerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(request));
  }

  @GetMapping
  @Operation(summary = "List trainers (paginated)")
  public ResponseEntity<PageResponse<TrainerResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(trainerService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a trainer by id")
  public ResponseEntity<TrainerResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(trainerService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Update a trainer")
  public ResponseEntity<TrainerResponse> update(
      @PathVariable Long id, @Valid @RequestBody TrainerRequest request) {
    return ResponseEntity.ok(trainerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN','BRANCH_MANAGER')")
  @Operation(summary = "Delete a trainer")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    trainerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
