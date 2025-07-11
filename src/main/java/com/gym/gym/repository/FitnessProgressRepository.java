package com.gym.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.gym.model.FitnessProgress;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FitnessProgressRepository extends JpaRepository<FitnessProgress, Long> {
    List<FitnessProgress> findByMember_Id(Long memberId);
    List<FitnessProgress> findByMember_IdAndDateBetween(Long memberId, LocalDate start, LocalDate end);
    List<FitnessProgress> findByMember_IdOrderByDateDesc(Long memberId);
}