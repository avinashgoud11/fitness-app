package com.gym.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.gym.model.Trainer;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByAvailableTrue();
    List<Trainer> findBySpecializationsContaining(String specialization);
    List<Trainer> findByUser_Email(String email);
} 