package com.example.app.service;

import com.example.app.dto.fitnessclass.FitnessClassRequest;
import com.example.app.dto.fitnessclass.FitnessClassResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.BranchStatus;
import com.example.app.entity.ClassStatus;
import com.example.app.entity.FitnessClass;
import com.example.app.entity.RegistrationStatus;
import com.example.app.entity.Trainer;
import com.example.app.entity.TrainerStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.ClassRegistrationRepository;
import com.example.app.repository.FitnessClassRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FitnessClassService {

  private final FitnessClassRepository fitnessClassRepository;
  private final ClassRegistrationRepository classRegistrationRepository;
  private final BranchService branchService;
  private final TrainerService trainerService;

  public FitnessClassService(
      FitnessClassRepository fitnessClassRepository,
      ClassRegistrationRepository classRegistrationRepository,
      BranchService branchService,
      TrainerService trainerService) {
    this.fitnessClassRepository = fitnessClassRepository;
    this.classRegistrationRepository = classRegistrationRepository;
    this.branchService = branchService;
    this.trainerService = trainerService;
  }

  public FitnessClassResponse create(FitnessClassRequest request) {
    Branch branch = branchService.findOrThrow(request.getBranchId());
    if (branch.getStatus() == BranchStatus.CLOSED
        || branch.getStatus() == BranchStatus.UNDER_MAINTENANCE) {
      throw new BusinessRuleException(
          "Branch is not accepting new bookings: " + branch.getStatus());
    }
    Trainer trainer = trainerService.findOrThrow(request.getTrainerId());
    if (trainer.getStatus() != TrainerStatus.ACTIVE) {
      throw new BusinessRuleException(
          "Only active trainers can be scheduled for classes: " + trainer.getId());
    }
    if (!request.getEndTime().isAfter(request.getStartTime())) {
      throw new BusinessRuleException("endTime must be after startTime");
    }

    FitnessClass fitnessClass =
        FitnessClass.builder()
            .name(request.getName())
            .classType(request.getClassType())
            .branchId(branch.getId())
            .trainerId(trainer.getId())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .capacity(request.getCapacity())
            .status(ClassStatus.SCHEDULED)
            .build();
    return toResponse(fitnessClassRepository.save(fitnessClass));
  }

  public Page<FitnessClassResponse> list(Pageable pageable) {
    return fitnessClassRepository.findAll(pageable).map(this::toResponse);
  }

  public FitnessClassResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public FitnessClassResponse cancel(String id) {
    FitnessClass fitnessClass = findOrThrow(id);
    fitnessClass.setStatus(ClassStatus.CANCELLED);
    return toResponse(fitnessClassRepository.save(fitnessClass));
  }

  public void delete(String id) {
    fitnessClassRepository.delete(findOrThrow(id));
  }

  public FitnessClass findOrThrow(String id) {
    return fitnessClassRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found: " + id));
  }

  FitnessClassResponse toResponse(FitnessClass fitnessClass) {
    long registered =
        classRegistrationRepository.countByFitnessClassIdAndStatus(
            fitnessClass.getId(), RegistrationStatus.REGISTERED);
    long waitlisted =
        classRegistrationRepository.countByFitnessClassIdAndStatus(
            fitnessClass.getId(), RegistrationStatus.WAITLISTED);
    String branchName = branchService.findOrThrow(fitnessClass.getBranchId()).getName();
    Trainer trainer = trainerService.findOrThrow(fitnessClass.getTrainerId());
    return FitnessClassResponse.builder()
        .id(fitnessClass.getId())
        .name(fitnessClass.getName())
        .classType(fitnessClass.getClassType())
        .branchId(fitnessClass.getBranchId())
        .branchName(branchName)
        .trainerId(trainer.getId())
        .trainerName(trainer.getFirstName() + " " + trainer.getLastName())
        .startTime(fitnessClass.getStartTime())
        .endTime(fitnessClass.getEndTime())
        .capacity(fitnessClass.getCapacity())
        .registeredCount(registered)
        .waitlistCount(waitlisted)
        .status(fitnessClass.getStatus())
        .build();
  }
}
