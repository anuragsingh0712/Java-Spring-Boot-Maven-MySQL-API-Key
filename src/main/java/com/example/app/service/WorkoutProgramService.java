package com.example.app.service;

import com.example.app.dto.workout.ExerciseRequest;
import com.example.app.dto.workout.ExerciseResponse;
import com.example.app.dto.workout.WorkoutProgramRequest;
import com.example.app.dto.workout.WorkoutProgramResponse;
import com.example.app.entity.Exercise;
import com.example.app.entity.Trainer;
import com.example.app.entity.TrainerStatus;
import com.example.app.entity.WorkoutProgram;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.WorkoutProgramRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WorkoutProgramService {

  private final WorkoutProgramRepository workoutProgramRepository;
  private final TrainerService trainerService;

  public WorkoutProgramService(
      WorkoutProgramRepository workoutProgramRepository, TrainerService trainerService) {
    this.workoutProgramRepository = workoutProgramRepository;
    this.trainerService = trainerService;
  }

  public WorkoutProgramResponse create(WorkoutProgramRequest request) {
    Trainer trainer = null;
    if (request.getTrainerId() != null) {
      trainer = trainerService.findOrThrow(request.getTrainerId());
      if (trainer.getStatus() != TrainerStatus.ACTIVE) {
        throw new BusinessRuleException(
            "Only active trainers can create trainer-specific programs: " + trainer.getId());
      }
    }

    WorkoutProgram program =
        WorkoutProgram.builder()
            .name(request.getName())
            .description(request.getDescription())
            .level(request.getLevel())
            .trainerId(trainer != null ? trainer.getId() : null)
            .exercises(new ArrayList<>())
            .build();

    addExercises(program, request.getExercises());

    return toResponse(workoutProgramRepository.save(program));
  }

  public Page<WorkoutProgramResponse> list(Pageable pageable) {
    return workoutProgramRepository.findAll(pageable).map(this::toResponse);
  }

  public WorkoutProgramResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public WorkoutProgramResponse update(String id, WorkoutProgramRequest request) {
    WorkoutProgram program = findOrThrow(id);
    Trainer trainer = null;
    if (request.getTrainerId() != null) {
      trainer = trainerService.findOrThrow(request.getTrainerId());
      if (trainer.getStatus() != TrainerStatus.ACTIVE) {
        throw new BusinessRuleException(
            "Only active trainers can manage trainer-specific programs: " + trainer.getId());
      }
    }
    program.setName(request.getName());
    program.setDescription(request.getDescription());
    program.setLevel(request.getLevel());
    program.setTrainerId(trainer != null ? trainer.getId() : null);
    program.getExercises().clear();
    addExercises(program, request.getExercises());
    return toResponse(workoutProgramRepository.save(program));
  }

  public void delete(String id) {
    workoutProgramRepository.delete(findOrThrow(id));
  }

  public WorkoutProgram findOrThrow(String id) {
    return workoutProgramRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Workout program not found: " + id));
  }

  private void addExercises(WorkoutProgram program, List<ExerciseRequest> exerciseRequests) {
    if (exerciseRequests == null || exerciseRequests.isEmpty()) {
      throw new BusinessRuleException("A workout program must contain at least one valid exercise");
    }
    int index = 0;
    for (ExerciseRequest er : exerciseRequests) {
      Exercise exercise =
          Exercise.builder()
              .id(UUID.randomUUID().toString())
              .name(er.getName())
              .category(er.getCategory())
              .sets(er.getSets())
              .reps(er.getReps())
              .durationSeconds(er.getDurationSeconds())
              .orderIndex(er.getOrderIndex() != null ? er.getOrderIndex() : index)
              .build();
      program.getExercises().add(exercise);
      index++;
    }
  }

  private WorkoutProgramResponse toResponse(WorkoutProgram program) {
    List<ExerciseResponse> exercises =
        program.getExercises().stream()
            .sorted(
                Comparator.comparing(
                    Exercise::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(
                e ->
                    ExerciseResponse.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .category(e.getCategory())
                        .sets(e.getSets())
                        .reps(e.getReps())
                        .durationSeconds(e.getDurationSeconds())
                        .orderIndex(e.getOrderIndex())
                        .build())
            .toList();
    Trainer trainer =
        program.getTrainerId() != null ? trainerService.findOrThrow(program.getTrainerId()) : null;
    return WorkoutProgramResponse.builder()
        .id(program.getId())
        .name(program.getName())
        .description(program.getDescription())
        .level(program.getLevel())
        .trainerId(program.getTrainerId())
        .trainerName(trainer != null ? trainer.getFirstName() + " " + trainer.getLastName() : null)
        .exercises(exercises)
        .build();
  }
}
