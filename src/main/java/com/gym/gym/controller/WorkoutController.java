package com.gym.gym.controller;

import com.gym.gym.model.Workout;
import com.gym.gym.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        return ResponseEntity.ok(workoutService.saveWorkout(workout));
    }

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return ResponseEntity.ok(workoutService.getAllWorkouts());
    }

    @GetMapping("workouts/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Workout>> getWorkoutsByDate(@PathVariable String date) {
        return ResponseEntity.ok(workoutService.getWorkoutsByDate(LocalDate.parse(date)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Workout>> getWorkoutsByType(@PathVariable String type) {
        return ResponseEntity.ok(workoutService.getWorkoutsByType(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.ok().build();
    }
} 