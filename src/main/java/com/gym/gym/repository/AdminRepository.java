package com.gym.gym.repository;

import com.gym.gym.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * Find admin by admin code
     */
    Optional<Admin> findByAdminCode(String adminCode);
    
    /**
     * Find admin by user ID
     */
    Optional<Admin> findByUserId(Long userId);
    
    /**
     * Find admin by email
     */
    Optional<Admin> findByUserEmail(String email);
    
    /**
     * Find all active admins
     */
    List<Admin> findByActiveTrue();
    
    /**
     * Find admins by department
     */
    List<Admin> findByDepartment(String department);
    
    /**
     * Find admins by access level
     */
    List<Admin> findByAccessLevel(String accessLevel);
    
    /**
     * Find admins by department and active status
     */
    List<Admin> findByDepartmentAndActiveTrue(String department);
    
    /**
     * Check if admin code exists
     */
    boolean existsByAdminCode(String adminCode);
    
    /**
     * Check if user is admin
     */
    boolean existsByUserId(Long userId);
} 