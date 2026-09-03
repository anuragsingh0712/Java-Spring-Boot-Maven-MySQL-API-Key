package com.example.app.controller;

import com.example.app.dto.PageResponse;
import com.example.app.dto.gym.GymRequest;
import com.example.app.dto.gym.GymResponse;
import com.example.app.service.GymService;
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
@RequestMapping("/api/v1/gyms")
@Tag(name = "Gyms", description = "Gym management endpoints")
public class GymController {

  private final GymService gymService;

  public GymController(GymService gymService) {
    this.gymService = gymService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Create a gym")
  public ResponseEntity<GymResponse> create(@Valid @RequestBody GymRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(gymService.create(request));
  }

  @GetMapping
  @Operation(summary = "List gyms (paginated)")
  public ResponseEntity<PageResponse<GymResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(PageResponse.of(gymService.list(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a gym by id")
  public ResponseEntity<GymResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(gymService.get(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Update a gym")
  public ResponseEntity<GymResponse> update(
      @PathVariable String id, @Valid @RequestBody GymRequest request) {
    return ResponseEntity.ok(gymService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','GYM_ADMIN')")
  @Operation(summary = "Delete a gym")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    gymService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
