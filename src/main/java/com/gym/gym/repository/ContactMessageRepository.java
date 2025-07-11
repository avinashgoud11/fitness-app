package com.gym.gym.repository;

import org.springframework.stereotype.Repository;

import com.gym.gym.model.ContactMessage;
import com.gym.gym.model.MessageStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByStatus(MessageStatus status);
    List<ContactMessage> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);
    List<ContactMessage> findByEmail(String email);
    List<ContactMessage> findByStatusOrderBySubmissionDateDesc(String status);
    long countByStatus(MessageStatus status);
    List<ContactMessage> findTopByOrderBySubmissionDateDesc(Pageable pageable);
    List<ContactMessage> findTop5ByOrderBySubmissionDateDesc(); // replace 5 with any number

    
    @Query("SELECT m FROM ContactMessage m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ContactMessage> searchByKeyword(@Param("keyword") String keyword);
} 