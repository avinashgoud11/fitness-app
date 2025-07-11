package com.gym.gym.repository;

import org.springframework.stereotype.Repository;

import com.gym.gym.model.ClassBooking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByMemberId(Long memberId);
    List<ClassBooking> findByFitnessClassId(Long classId);
    List<ClassBooking> findByStatus(String status);
    int countByFitnessClassIdAndStatus(Long classId, String status);
    boolean existsByMemberIdAndFitnessClassIdAndStatus(Long memberId, Long classId, String status);
}