package com.gym.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.gym.model.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMember_Id(Long memberId);
    List<Payment> findByStatus(String status);
    List<Payment> findByMember_IdAndStatus(Long memberId, String status);
    List<Payment> findByFitnessClass_Id(Long classId);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findByMember_IdAndPaymentDateBetween(Long memberId, LocalDateTime start, LocalDateTime end);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByMember_IdOrderByCreatedAtDesc(Long memberId);
    List<Payment> findByStatusAndPaymentDateBefore(String status, LocalDateTime date);
} 