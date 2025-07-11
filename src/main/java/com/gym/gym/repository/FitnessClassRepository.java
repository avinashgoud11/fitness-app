package com.gym.gym.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.gym.gym.model.ClassLevel;
import com.gym.gym.model.FitnessClass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
    List<FitnessClass> findByTrainerId(Long trainerId);
    List<FitnessClass> findByLevel(String level);
    List<FitnessClass> findByLevel(ClassLevel level);
    List<FitnessClass> findByStartTimeAfter(LocalDateTime time);
    List<FitnessClass> findByCurrentEnrollmentLessThan(int maxCapacity);
    List<FitnessClass> findByRequiredEquipmentContaining(String equipment);
    List<FitnessClass> findByRoom(String room);
    List<FitnessClass> findByPriceBetween(double minPrice, double maxPrice);
    
    @Query("SELECT f FROM FitnessClass f WHERE " +
    "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(f.room) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FitnessClass> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT f FROM FitnessClass f WHERE f.currentEnrollment < f.maxCapacity")
    List<FitnessClass> findByCurrentEnrollmentLessThanMaxCapacity();
} 