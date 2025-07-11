package com.gym.gym.service;

import com.gym.gym.model.Workout;
import com.gym.gym.repository.WorkoutRepository;

// Ensure the correct package path for WorkoutRepository
// Removed unused and conflicting import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class WorkoutService {
    
    private final WorkoutRepository workoutRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public Workout saveWorkout(Workout workout) {
        return workoutRepository.save(workout);
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAll();
    }

    public List<Workout> getWorkoutsByDate(LocalDate date) {
        return workoutRepository.findByDate(date);
    }

    public List<Workout> getWorkoutsByType(String type) {
        return workoutRepository.findByType(type);
    }

    public void deleteWorkout(Long id) {
        workoutRepository.deleteById(id);
    }

    public Workout getWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + id));
    }
} 