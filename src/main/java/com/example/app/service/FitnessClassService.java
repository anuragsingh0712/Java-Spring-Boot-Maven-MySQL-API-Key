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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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

  @Transactional
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
            .branch(branch)
            .trainer(trainer)
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

  public FitnessClassResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public FitnessClassResponse cancel(Long id) {
    FitnessClass fitnessClass = findOrThrow(id);
    fitnessClass.setStatus(ClassStatus.CANCELLED);
    return toResponse(fitnessClassRepository.save(fitnessClass));
  }

  @Transactional
  public void delete(Long id) {
    fitnessClassRepository.delete(findOrThrow(id));
  }

  public FitnessClass findOrThrow(Long id) {
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
    return FitnessClassResponse.builder()
        .id(fitnessClass.getId())
        .name(fitnessClass.getName())
        .classType(fitnessClass.getClassType())
        .branchId(fitnessClass.getBranch().getId())
        .branchName(fitnessClass.getBranch().getName())
        .trainerId(fitnessClass.getTrainer().getId())
        .trainerName(
            fitnessClass.getTrainer().getFirstName()
                + " "
                + fitnessClass.getTrainer().getLastName())
        .startTime(fitnessClass.getStartTime())
        .endTime(fitnessClass.getEndTime())
        .capacity(fitnessClass.getCapacity())
        .registeredCount(registered)
        .waitlistCount(waitlisted)
        .status(fitnessClass.getStatus())
        .build();
  }
}
