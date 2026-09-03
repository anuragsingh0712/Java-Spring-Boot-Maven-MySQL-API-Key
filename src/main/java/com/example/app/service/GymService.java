package com.example.app.service;

import com.example.app.dto.gym.GymRequest;
import com.example.app.dto.gym.GymResponse;
import com.example.app.entity.Gym;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.GymRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GymService {

  private final GymRepository gymRepository;

  public GymService(GymRepository gymRepository) {
    this.gymRepository = gymRepository;
  }

  @Transactional
  public GymResponse create(GymRequest request) {
    Gym gym =
        Gym.builder()
            .name(request.getName())
            .registrationNumber(request.getRegistrationNumber())
            .contactEmail(request.getContactEmail())
            .contactPhone(request.getContactPhone())
            .description(request.getDescription())
            .build();
    return toResponse(gymRepository.save(gym));
  }

  public Page<GymResponse> list(Pageable pageable) {
    return gymRepository.findAll(pageable).map(this::toResponse);
  }

  public GymResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public GymResponse update(Long id, GymRequest request) {
    Gym gym = findOrThrow(id);
    gym.setName(request.getName());
    gym.setRegistrationNumber(request.getRegistrationNumber());
    gym.setContactEmail(request.getContactEmail());
    gym.setContactPhone(request.getContactPhone());
    gym.setDescription(request.getDescription());
    return toResponse(gymRepository.save(gym));
  }

  @Transactional
  public void delete(Long id) {
    Gym gym = findOrThrow(id);
    gymRepository.delete(gym);
  }

  private Gym findOrThrow(Long id) {
    return gymRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Gym not found: " + id));
  }

  private GymResponse toResponse(Gym gym) {
    return GymResponse.builder()
        .id(gym.getId())
        .name(gym.getName())
        .registrationNumber(gym.getRegistrationNumber())
        .contactEmail(gym.getContactEmail())
        .contactPhone(gym.getContactPhone())
        .description(gym.getDescription())
        .createdAt(gym.getCreatedAt())
        .updatedAt(gym.getUpdatedAt())
        .build();
  }
}
