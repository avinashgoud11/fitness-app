package com.gym.gym.controller;

import com.gym.gym.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Collections;

// Import ClassAnalyticsResponse (adjust the package if needed)
import com.gym.gym.dto.ClassAnalyticsResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get overall dashboard overview
     * @return Dashboard statistics
     */
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        try {
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load dashboard overview"));
        }
    }

    /**
     * Get member statistics
     * @return Member analytics
     */
    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> getMemberStatistics() {
        try {
            Map<String, Object> stats = dashboardService.getMemberStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load member statistics"));
        }
    }

    /**
     * Get revenue analytics
     * @return Revenue statistics
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {
        try {
            Map<String, Object> analytics = dashboardService.getRevenueAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load revenue analytics"));
        }
    }

    /**
     * Get class analytics
     * @return Class statistics
     */
@GetMapping("/classes")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
public ResponseEntity<Map<String, Object>> getClassAnalytics() {
    try {
        Map<String, Object> analytics = dashboardService.getClassAnalytics();
        return ResponseEntity.ok(analytics);
    } catch (Exception e) {
        log.error("Error while loading class analytics", e); // Remove or implement logging if needed
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to load class analytics"));
    }
}

    /**
     * Get trainer analytics
     * @return Trainer statistics
     */
@GetMapping("/trainers")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Map<String, Object>> getTrainerAnalytics() {
    try {
        Map<String, Object> analytics = dashboardService.getTrainerAnalytics();
        return ResponseEntity.ok(analytics);
    } catch (Exception e) {
        e.printStackTrace(); // <--- This will print the stack trace to System.err
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to load trainer analytics"));
    }
}

    /**
     * Get all dashboard data in one request
     * @return Complete dashboard data
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllDashboardData() {
        try {
            Map<String, Object> allData = Map.of(
                "overview", dashboardService.getDashboardOverview(),
                "members", dashboardService.getMemberStatistics(),
                "revenue", dashboardService.getRevenueAnalytics(),
                "classes", dashboardService.getClassAnalytics(),
                "trainers", dashboardService.getTrainerAnalytics()
            );
            return ResponseEntity.ok(allData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load dashboard data"));
        }
    }

    /**
     * Get dashboard data for trainers (limited access)
     * @return Trainer dashboard data
     */
    @GetMapping("/trainer-view")
    @PreAuthorize("hasRole('ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> getTrainerDashboard() {
        try {
            Map<String, Object> trainerData = Map.of(
                "overview", dashboardService.getDashboardOverview(),
                "classes", dashboardService.getClassAnalytics(),
                "members", dashboardService.getMemberStatistics()
            );
            return ResponseEntity.ok(trainerData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load trainer dashboard"));
        }
    }

    /**
     * Get dashboard data for members (basic access)
     * @return Member dashboard data
     */
    @GetMapping("/member-view")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<Map<String, Object>> getMemberDashboard() {
        try {
            Map<String, Object> memberData = Map.of(
                "overview", dashboardService.getDashboardOverview(),
                "classes", dashboardService.getClassAnalytics()
            );
            return ResponseEntity.ok(memberData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load member dashboard"));
        }
    }
} 