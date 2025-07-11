package com.gym.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gym.gym.model.Workout;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByDate(LocalDate date);
    List<Workout> findByType(String type);
    List<Workout> findByDateAndType(LocalDate date, String type);
} 