package com.gym.gym.repository;

import com.gym.gym.model.ClassLevel; // Assuming you have this enum
import com.gym.gym.model.Gender;     // Assuming you have this enum
import com.gym.gym.model.MembershipType; // Assuming you have this enum

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for fetching aggregated dashboard statistics.
 * This does not extend JpaRepository as it deals with cross-entity data.
 */
public interface DashboardRepository {

    // --- Member Statistics ---
    long getTotalMembersCount();
    long getActiveMembersCount();
    long getInactiveMembersCount();
    long getNewMembersCountSince(LocalDateTime dateTime); // More generic for month/week
    double getMembershipGrowthPercentage(LocalDateTime previousPeriodEnd); // Needs current total

    Map<String, Long> getMembershipDistributionData();
    Map<String, Long> getGenderDistributionData();
    // Map<String, Long> getAgeDistributionData(); // To be implemented later
    // Map<String, Long> getMemberActivityByMonthData(); // To be implemented later

    // --- Revenue Statistics ---
    double getTotalRevenueAmount();
    double getRevenueAmountSince(LocalDateTime dateTime); // More generic for monthly/weekly revenue
    double getRevenueGrowthPercentage(LocalDateTime previousPeriodEnd); // Needs current total
    long getPendingPaymentsCount();
    long getPaidPaymentsCount();

    Map<String, Double> getRevenueByPaymentMethodData();
    List<Map<String, Object>> getRevenueByClassData();
    // List<Map<String, Object>> getRevenueTrendsData(); // To be implemented later
    double getAverageTransactionValue();

    // --- Class Statistics ---
    long getTotalClassesCount();
    long getActiveClassesCount();
    long getTotalBookingsCount();
    double getAverageClassAttendanceValue();
    List<Map<String, Object>> getMostPopularClassesData();
    double getClassUtilizationPercentage();
    // List<Map<String, Object>> getAttendanceTrendsData(); // To be implemented later
    Map<String, Long> getClassLevelDistributionData();

    // --- Trainer Statistics ---
    long getTotalTrainersCount();
    long getActiveTrainersCount();
    List<Map<String, Object>> getTrainerPerformanceData();
    List<Map<String, Object>> getTrainerWorkloadData();

    // --- Recent Activity ---
    List<Map<String, Object>> getRecentBookingsData(int limit);
    List<Map<String, Object>> getRecentPaymentsData(int limit);
    List<Map<String, Object>> getRecentMessagesData(int limit);
}