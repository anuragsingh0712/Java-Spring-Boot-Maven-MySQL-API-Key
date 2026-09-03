package com.example.app.repository;

import com.example.app.entity.WorkoutProgram;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkoutProgramRepository extends MongoRepository<WorkoutProgram, String> {}
