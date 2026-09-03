package com.example.app.service;

import com.example.app.dto.trainer.TrainerRequest;
import com.example.app.dto.trainer.TrainerResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.Trainer;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.TrainerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {

  private final TrainerRepository trainerRepository;
  private final BranchService branchService;

  public TrainerService(TrainerRepository trainerRepository, BranchService branchService) {
    this.trainerRepository = trainerRepository;
    this.branchService = branchService;
  }

  public TrainerResponse create(TrainerRequest request) {
    Branch branch = branchService.findOrThrow(request.getBranchId());
    Trainer trainer =
        Trainer.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .specialization(request.getSpecialization())
            .certifications(request.getCertifications())
            .experienceYears(request.getExperienceYears())
            .branchId(branch.getId())
            .status(request.getStatus())
            .build();
    return toResponse(trainerRepository.save(trainer));
  }

  public Page<TrainerResponse> list(Pageable pageable) {
    return trainerRepository.findAll(pageable).map(this::toResponse);
  }

  public TrainerResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public TrainerResponse update(String id, TrainerRequest request) {
    Trainer trainer = findOrThrow(id);
    Branch branch = branchService.findOrThrow(request.getBranchId());
    trainer.setFirstName(request.getFirstName());
    trainer.setLastName(request.getLastName());
    trainer.setEmail(request.getEmail());
    trainer.setPhone(request.getPhone());
    trainer.setSpecialization(request.getSpecialization());
    trainer.setCertifications(request.getCertifications());
    trainer.setExperienceYears(request.getExperienceYears());
    trainer.setBranchId(branch.getId());
    trainer.setStatus(request.getStatus());
    return toResponse(trainerRepository.save(trainer));
  }

  public void delete(String id) {
    trainerRepository.delete(findOrThrow(id));
  }

  public Trainer findOrThrow(String id) {
    return trainerRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer not found: " + id));
  }

  private TrainerResponse toResponse(Trainer trainer) {
    String branchName = branchService.findOrThrow(trainer.getBranchId()).getName();
    return TrainerResponse.builder()
        .id(trainer.getId())
        .firstName(trainer.getFirstName())
        .lastName(trainer.getLastName())
        .email(trainer.getEmail())
        .phone(trainer.getPhone())
        .specialization(trainer.getSpecialization())
        .certifications(trainer.getCertifications())
        .experienceYears(trainer.getExperienceYears())
        .branchId(trainer.getBranchId())
        .branchName(branchName)
        .status(trainer.getStatus())
        .build();
  }
}
