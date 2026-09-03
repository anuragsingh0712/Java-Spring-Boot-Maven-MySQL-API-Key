package com.example.app.service;

import com.example.app.dto.trainer.TrainerRequest;
import com.example.app.dto.trainer.TrainerResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.Trainer;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.BranchRepository;
import com.example.app.repository.TrainerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TrainerService {

  private final TrainerRepository trainerRepository;
  private final BranchRepository branchRepository;

  public TrainerService(TrainerRepository trainerRepository, BranchRepository branchRepository) {
    this.trainerRepository = trainerRepository;
    this.branchRepository = branchRepository;
  }

  @Transactional
  public TrainerResponse create(TrainerRequest request) {
    Branch branch =
        branchRepository
            .findById(request.getBranchId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Branch not found: " + request.getBranchId()));
    Trainer trainer =
        Trainer.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .specialization(request.getSpecialization())
            .certifications(request.getCertifications())
            .experienceYears(request.getExperienceYears())
            .branch(branch)
            .status(request.getStatus())
            .build();
    return toResponse(trainerRepository.save(trainer));
  }

  public Page<TrainerResponse> list(Pageable pageable) {
    return trainerRepository.findAll(pageable).map(this::toResponse);
  }

  public TrainerResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public TrainerResponse update(Long id, TrainerRequest request) {
    Trainer trainer = findOrThrow(id);
    Branch branch =
        branchRepository
            .findById(request.getBranchId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Branch not found: " + request.getBranchId()));
    trainer.setFirstName(request.getFirstName());
    trainer.setLastName(request.getLastName());
    trainer.setEmail(request.getEmail());
    trainer.setPhone(request.getPhone());
    trainer.setSpecialization(request.getSpecialization());
    trainer.setCertifications(request.getCertifications());
    trainer.setExperienceYears(request.getExperienceYears());
    trainer.setBranch(branch);
    trainer.setStatus(request.getStatus());
    return toResponse(trainerRepository.save(trainer));
  }

  @Transactional
  public void delete(Long id) {
    trainerRepository.delete(findOrThrow(id));
  }

  public Trainer findOrThrow(Long id) {
    return trainerRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer not found: " + id));
  }

  private TrainerResponse toResponse(Trainer trainer) {
    return TrainerResponse.builder()
        .id(trainer.getId())
        .firstName(trainer.getFirstName())
        .lastName(trainer.getLastName())
        .email(trainer.getEmail())
        .phone(trainer.getPhone())
        .specialization(trainer.getSpecialization())
        .certifications(trainer.getCertifications())
        .experienceYears(trainer.getExperienceYears())
        .branchId(trainer.getBranch().getId())
        .branchName(trainer.getBranch().getName())
        .status(trainer.getStatus())
        .build();
  }
}
