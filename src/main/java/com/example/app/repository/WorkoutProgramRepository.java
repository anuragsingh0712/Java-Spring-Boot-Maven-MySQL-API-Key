package com.example.app.repository;

import com.example.app.entity.WorkoutProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long> {}
