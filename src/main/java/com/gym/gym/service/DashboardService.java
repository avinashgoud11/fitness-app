package com.gym.gym.service;

import com.gym.gym.model.*;
import com.gym.gym.repository.*; // Keep these imports if some direct JpaRepository calls are still needed
import com.gym.gym.repository.DashboardRepository; // Import the new custom repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Keep for methods that still process lazy data

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    // Autowire your custom DashboardRepository
    @Autowired
    private DashboardRepository dashboardRepository;

    // Keep these if any helper methods still directly use them for calculations not moved to DashboardRepository
    @Autowired private MemberRepository memberRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private FitnessClassRepository fitnessClassRepository;
    @Autowired private ClassBookingRepository classBookingRepository;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactMessageRepository contactMessageRepository;


    /**
     * Get overall dashboard statistics
     * @return Dashboard overview
     */
    @Transactional(readOnly = true) // Keep transactional for any potential lazy loading within this method
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> dashboard = new HashMap<>();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        // Member statistics
        dashboard.put("totalMembers", dashboardRepository.getTotalMembersCount());
        dashboard.put("activeMembers", dashboardRepository.getActiveMembersCount());
        dashboard.put("newMembersThisMonth", dashboardRepository.getNewMembersCountSince(startOfMonth));
        dashboard.put("membershipGrowth", dashboardRepository.getMembershipGrowthPercentage(lastMonthStart));

        // Revenue statistics
        dashboard.put("totalRevenue", dashboardRepository.getTotalRevenueAmount());
        dashboard.put("monthlyRevenue", dashboardRepository.getRevenueAmountSince(startOfMonth));
        dashboard.put("revenueGrowth", dashboardRepository.getRevenueGrowthPercentage(lastMonthStart));
        dashboard.put("pendingPayments", dashboardRepository.getPendingPaymentsCount());

        // Class statistics
        dashboard.put("totalClasses", dashboardRepository.getTotalClassesCount());
        dashboard.put("activeClasses", dashboardRepository.getActiveClassesCount());
        dashboard.put("totalBookings", dashboardRepository.getTotalBookingsCount());
        dashboard.put("averageClassAttendance", dashboardRepository.getAverageClassAttendanceValue());

        // Trainer statistics
        dashboard.put("totalTrainers", dashboardRepository.getTotalTrainersCount());
        dashboard.put("activeTrainers", dashboardRepository.getActiveTrainersCount());

        // Recent activity
        dashboard.put("recentBookings", dashboardRepository.getRecentBookingsData(10));
        dashboard.put("recentPayments", dashboardRepository.getRecentPaymentsData(10));
        dashboard.put("recentMessages", dashboardRepository.getRecentMessagesData(10));

        return dashboard;
    }

    /**
     * Get member statistics
     * @return Member analytics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMemberStatistics() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        stats.put("totalMembers", dashboardRepository.getTotalMembersCount());
        stats.put("activeMembers", dashboardRepository.getActiveMembersCount());
        stats.put("inactiveMembers", dashboardRepository.getInactiveMembersCount());
        stats.put("newMembersThisMonth", dashboardRepository.getNewMembersCountSince(startOfMonth));
        stats.put("newMembersThisWeek", dashboardRepository.getNewMembersCountSince(startOfWeek));
        stats.put("membershipGrowth", dashboardRepository.getMembershipGrowthPercentage(lastMonthStart));

        // Membership type distribution
        stats.put("membershipDistribution", dashboardRepository.getMembershipDistributionData());

        // Gender distribution
        stats.put("genderDistribution", dashboardRepository.getGenderDistributionData());

        // Age distribution (still empty as per DashboardRepository)
        stats.put("ageDistribution", new HashMap<>());

        // Member activity by month (still empty as per DashboardRepository)
        stats.put("memberActivityByMonth", new HashMap<>());

        return stats;
    }

    /**
     * Get revenue analytics
     * @return Revenue statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        analytics.put("totalRevenue", dashboardRepository.getTotalRevenueAmount());
        analytics.put("monthlyRevenue", dashboardRepository.getRevenueAmountSince(startOfMonth));
        analytics.put("weeklyRevenue", dashboardRepository.getRevenueAmountSince(startOfWeek));
        analytics.put("revenueGrowth", dashboardRepository.getRevenueGrowthPercentage(lastMonthStart));
        analytics.put("pendingPayments", dashboardRepository.getPendingPaymentsCount());
        analytics.put("paidPayments", dashboardRepository.getPaidPaymentsCount());

        // Revenue by payment method
        analytics.put("revenueByPaymentMethod", dashboardRepository.getRevenueByPaymentMethodData());

        // Revenue by class
        analytics.put("revenueByClass", dashboardRepository.getRevenueByClassData());

        // Revenue trends (still empty as per DashboardRepository)
        analytics.put("revenueTrends", new ArrayList<>());

        // Average transaction value
        analytics.put("averageTransactionValue", dashboardRepository.getAverageTransactionValue());

        return analytics;
    }

    /**
     * Get class analytics
     * @return Class statistics
     */
    @Transactional(readOnly = true) // Keep transactional for any potential lazy loading within this method
    public Map<String, Object> getClassAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalClasses", dashboardRepository.getTotalClassesCount());
        analytics.put("activeClasses", dashboardRepository.getActiveClassesCount());
        analytics.put("totalBookings", dashboardRepository.getTotalBookingsCount());
        analytics.put("averageClassAttendance", dashboardRepository.getAverageClassAttendanceValue());
        analytics.put("mostPopularClasses", dashboardRepository.getMostPopularClassesData());
        analytics.put("classUtilization", dashboardRepository.getClassUtilizationPercentage()); // Updated to use new method

        // Class attendance trends (still empty as per DashboardRepository)
        analytics.put("attendanceTrends", new ArrayList<>());

        // Class level distribution
        analytics.put("classLevelDistribution", dashboardRepository.getClassLevelDistributionData());

        // Trainer performance
        analytics.put("trainerPerformance", dashboardRepository.getTrainerPerformanceData());

        return analytics;
    }

    /**
     * Get trainer analytics
     * @return Trainer statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTrainerAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalTrainers", dashboardRepository.getTotalTrainersCount());
        analytics.put("activeTrainers", dashboardRepository.getActiveTrainersCount());
        analytics.put("trainerPerformance", dashboardRepository.getTrainerPerformanceData());
        analytics.put("trainerWorkload", dashboardRepository.getTrainerWorkloadData()); // Updated to use new method

        return analytics;
    }

    // The methods below are now redundant as their logic has been moved to DashboardRepositoryImpl
    // You can remove them or keep them if they serve other purposes outside the dashboard.
    // private long getTotalMembers() { ... }
    // private long getActiveMembers() { ... }
    // private long getInactiveMembers() { ... }
    // private long getNewMembersThisMonth() { ... }
    // private long getNewMembersThisWeek() { ... }
    // private double getMembershipGrowth() { ... } // Replaced by dashboardRepository.getMembershipGrowthPercentage
    // private double getTotalRevenue() { ... }
    // private double getMonthlyRevenue() { ... }
    // private double getWeeklyRevenue() { ... } // Replaced by dashboardRepository.getRevenueAmountSince
    // private double getRevenueGrowth() { ... } // Replaced by dashboardRepository.getRevenueGrowthPercentage
    // private long getPendingPaymentsCount() { ... }
    // private long getPaidPaymentsCount() { ... }
    // private long getTotalClasses() { ... }
    // private long getActiveClasses() { ... }
    // private long getTotalBookings() { ... }
    // private double getAverageClassAttendance() { ... }
    // private long getTotalTrainers() { ... }
    // private long getActiveTrainers() { ... }
    // private List<Map<String, Object>> getRecentBookings() { ... }
    // private List<Map<String, Object>> getRecentPayments() { ... }
    // private List<Map<String, Object>> getRecentMessages() { ... }
    // private Map<String, Long> getMembershipDistribution() { ... }
    // private Map<String, Long> getGenderDistribution() { ... }
    // private Map<String, Long> getAgeDistribution() { ... }
    // private Map<String, Long> getMemberActivityByMonth() { ... }
    // private Map<String, Double> getRevenueByPaymentMethod() { ... }
    // private List<Map<String, Object>> getRevenueByClass() { ... }
    // private List<Map<String, Object>> getRevenueTrends() { ... }
    // private double getAverageTransactionValue() { ... }
    // private List<Map<String, Object>> getMostPopularClasses() { ... }
    // private double getClassUtilization() { ... }
    // private List<Map<String, Object>> getAttendanceTrends() { ... }
    // private Map<String, Long> getClassLevelDistribution() { ... }
    // private List<Map<String, Object>> getTrainerPerformance() { ... }
    // private List<Map<String, Object>> getTrainerWorkload() { ... }


    // --- Dashboard Views (remain in service as they combine data from custom repo) ---

    /**
     * Get all dashboard data in one request
     * @return Complete dashboard data
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllDashboardData() {
        // Re-use the existing methods to populate this map
        // Each of these methods is now @Transactional(readOnly = true)
        // so the session will remain open for their duration.
        return Map.of(
                "overview", getDashboardOverview(),
                "members", getMemberStatistics(),
                "revenue", getRevenueAnalytics(),
                "classes", getClassAnalytics(),
                "trainers", getTrainerAnalytics()
        );
    }

    /**
     * Get dashboard data for trainers (limited access)
     * @return Trainer dashboard data
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTrainerDashboard() {
        return Map.of(
                "overview", getDashboardOverview(),
                "classes", getClassAnalytics(),
                "members", getMemberStatistics()
        );
    }

    /**
     * Get dashboard data for members (basic access)
     * @return Member dashboard data
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMemberDashboard() {
        return Map.of(
                "overview", getDashboardOverview(),
                "classes", getClassAnalytics()
        );
    }
}