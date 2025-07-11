package com.gym.gym.repository;

import com.gym.gym.model.*; // Import all necessary models
import com.gym.gym.repository.*; // Import all necessary JpaRepositories
import com.gym.gym.repository.DashboardRepository; // Import the custom interface
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Important for lazy loading
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Implementation of DashboardRepository for fetching aggregated statistics.
 * It uses existing JpaRepositories to gather and process data.
 */
@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    // --- Member Statistics ---
    @Override
    public long getTotalMembersCount() {
        return memberRepository.count();
    }

    @Override
    public long getActiveMembersCount() {
        return memberRepository.findByActiveTrue().size();
    }

    @Override
    public long getInactiveMembersCount() {
        return memberRepository.count() - getActiveMembersCount();
    }

    @Override
    public long getNewMembersCountSince(LocalDateTime dateTime) {
        return memberRepository.findAll().stream()
                .filter(member -> member.getMembershipStartDate() != null && member.getMembershipStartDate().isAfter(dateTime))
                .count();
    }

    @Override
    public double getMembershipGrowthPercentage(LocalDateTime previousPeriodEnd) {
        long previousPeriodMembers = memberRepository.findAll().stream()
                .filter(member -> member.getMembershipStartDate() != null && member.getMembershipStartDate().isBefore(previousPeriodEnd))
                .count();
        long currentMembers = getTotalMembersCount();

        if (previousPeriodMembers == 0) return 0.0;
        return ((double) (currentMembers - previousPeriodMembers) / previousPeriodMembers) * 100;
    }

    @Override
    public Map<String, Long> getMembershipDistributionData() {
        Map<String, Long> distribution = new HashMap<>();
        List<Member> allMembers = memberRepository.findAll();
        distribution.put("BASIC", allMembers.stream().filter(m -> m.getMembershipType() == MembershipType.BASIC).count());
        distribution.put("PREMIUM", allMembers.stream().filter(m -> m.getMembershipType() == MembershipType.PREMIUM).count());
        distribution.put("VIP", allMembers.stream().filter(m -> m.getMembershipType() == MembershipType.VIP).count());
        return distribution;
    }

    @Override
    public Map<String, Long> getGenderDistributionData() {
        Map<String, Long> distribution = new HashMap<>();
        List<Member> allMembers = memberRepository.findAll();
        distribution.put("MALE", allMembers.stream().filter(m -> m.getGender() == Gender.MALE).count());
        distribution.put("FEMALE", allMembers.stream().filter(m -> m.getGender() == Gender.FEMALE).count());
        distribution.put("OTHER", allMembers.stream().filter(m -> m.getGender() == Gender.OTHER).count());
        return distribution;
    }

    // --- Revenue Statistics ---
    @Override
    public double getTotalRevenueAmount() {
        return paymentRepository.findByStatus("PAID")
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public double getRevenueAmountSince(LocalDateTime dateTime) {
        return paymentRepository.findByStatus("PAID").stream()
                .filter(payment -> payment.getPaymentDate() != null && payment.getPaymentDate().isAfter(dateTime))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public double getRevenueGrowthPercentage(LocalDateTime previousPeriodEnd) {
        double previousPeriodRevenue = paymentRepository.findByStatusAndPaymentDateBefore("PAID", previousPeriodEnd)
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        double currentPeriodRevenue = getRevenueAmountSince(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));

        if (previousPeriodRevenue == 0) return 0.0;
        return ((currentPeriodRevenue - previousPeriodRevenue) / previousPeriodRevenue) * 100;
    }

    @Override
    public long getPendingPaymentsCount() {
        return paymentRepository.findByStatus("PENDING").size();
    }

    @Override
    public long getPaidPaymentsCount() {
        return paymentRepository.findByStatus("PAID").size();
    }

    @Override
    public Map<String, Double> getRevenueByPaymentMethodData() {
        Map<String, Double> revenue = new HashMap<>();
        List<Payment> paidPayments = paymentRepository.findByStatus("PAID");
        revenue.put("CASH", paidPayments.stream()
                .filter(p -> "CASH".equals(p.getPaymentMethod()))
                .mapToDouble(Payment::getAmount).sum());
        revenue.put("CARD", paidPayments.stream()
                .filter(p -> "CARD".equals(p.getPaymentMethod()))
                .mapToDouble(Payment::getAmount).sum());
        revenue.put("ONLINE", paidPayments.stream()
                .filter(p -> "ONLINE".equals(p.getPaymentMethod()))
                .mapToDouble(Payment::getAmount).sum());
        return revenue;
    }

    @Override
    @Transactional(readOnly = true) // Ensure session is open for lazy loading if Payment.fitnessClass is lazy
    public List<Map<String, Object>> getRevenueByClassData() {
        // Fetch all payments. If Payment.fitnessClass is LAZY, this might cause N+1.
        // For better performance, consider a custom query in PaymentRepository
        // like @Query("SELECT p FROM Payment p JOIN FETCH p.fitnessClass WHERE p.status = 'PAID'")
        List<Payment> paidPayments = paymentRepository.findByStatus("PAID");

        return paidPayments.stream()
                .filter(payment -> payment.getFitnessClass() != null) // Defensive null check
                .collect(Collectors.groupingBy(
                        payment -> payment.getFitnessClass().getName(), // Group by class name
                        Collectors.summingDouble(Payment::getAmount) // Sum amounts
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> classRevenue = new HashMap<>();
                    classRevenue.put("className", entry.getKey());
                    classRevenue.put("revenue", entry.getValue());
                    return classRevenue;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("revenue"), (Double) a.get("revenue"))) // Sort by revenue
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageTransactionValue() {
        List<Payment> paidPayments = paymentRepository.findByStatus("PAID");
        if (paidPayments.isEmpty()) return 0.0;
        double totalAmount = paidPayments.stream().mapToDouble(Payment::getAmount).sum();
        return totalAmount / paidPayments.size();
    }

    // --- Class Statistics ---
    @Override
    public long getTotalClassesCount() {
        return fitnessClassRepository.count();
    }

    @Override
    public long getActiveClassesCount() {
        return fitnessClassRepository.findAll().stream()
                .filter(FitnessClass::isActive)
                .count();
    }

    @Override
    public long getTotalBookingsCount() {
        return classBookingRepository.count();
    }

    @Override
    @Transactional(readOnly = true) // Crucial for lazy-loaded enrollments
    public double getAverageClassAttendanceValue() {
        List<FitnessClass> classes = fitnessClassRepository.findAll(); // Fetches classes, enrollments are LAZY
        if (classes.isEmpty()) return 0.0;

        double totalAttendance = classes.stream()
                .mapToDouble(fitnessClass -> {
                    // This is where LazyInitializationException would occur if not transactional
                    // and if fitnessClass.getEnrollments() is LAZY and session closed.
                    // Assuming getCurrentEnrollment accesses the lazy collection.
                    return fitnessClass.getCurrentEnrollment();
                })
                .sum();
        return totalAttendance / classes.size();
    }

@Override
@Transactional(readOnly = true)
public List<Map<String, Object>> getMostPopularClassesData() {
    List<FitnessClass> allClasses = fitnessClassRepository.findAll();
    return allClasses.stream()
            .map(fitnessClass -> {
                Map<String, Object> classStats = new HashMap<>();
                classStats.put("className", fitnessClass.getName());
                classStats.put("enrollment", fitnessClass.getCurrentEnrollment());
                classStats.put("maxCapacity", fitnessClass.getMaxCapacity());

                double utilization = 0.0;
                if (fitnessClass.getMaxCapacity() > 0) {
                    utilization = (double) fitnessClass.getCurrentEnrollment() / fitnessClass.getMaxCapacity() * 100;
                }
                classStats.put("utilization", utilization);
                return classStats;
            })
            // Fix starts here: Safely convert Integer/Long to Double for comparison
            .sorted((a, b) -> {
                // Retrieve as Number to handle both Integer and Long
                Number enrollmentA = (Number) a.get("enrollment");
                Number enrollmentB = (Number) b.get("enrollment");
                return Double.compare(enrollmentB.doubleValue(), enrollmentA.doubleValue());
            })
            .limit(10)
            .collect(Collectors.toList());
}

    @Override
    @Transactional(readOnly = true) // Crucial for lazy-loaded enrollments
    public double getClassUtilizationPercentage() {
        List<FitnessClass> classes = fitnessClassRepository.findAll(); // Fetches classes, enrollments are LAZY
        if (classes.isEmpty()) return 0.0;

        double totalUtilization = classes.stream()
                .mapToDouble(class1 -> {
                    if (class1.getMaxCapacity() > 0) { // Defensive check for division by zero
                        return (double) class1.getCurrentEnrollment() / class1.getMaxCapacity();
                    }
                    return 0.0;
                })
                .sum();
        return (totalUtilization / classes.size()) * 100;
    }

    @Override
    public Map<String, Long> getClassLevelDistributionData() {
        Map<String, Long> distribution = new HashMap<>();
        List<FitnessClass> allClasses = fitnessClassRepository.findAll();
        distribution.put("BEGINNER", allClasses.stream().filter(c -> c.getLevel() == ClassLevel.BEGINNER).count());
        distribution.put("INTERMEDIATE", allClasses.stream().filter(c -> c.getLevel() == ClassLevel.INTERMEDIATE).count());
        distribution.put("ADVANCED", allClasses.stream().filter(c -> c.getLevel() == ClassLevel.ADVANCED).count());
        return distribution;
    }

    // --- Trainer Statistics ---
    @Override
    public long getTotalTrainersCount() {
        return trainerRepository.count();
    }

    @Override
    public long getActiveTrainersCount() {
        return trainerRepository.findAll().stream()
                .filter(Trainer::getAvailable)
                .count();
    }

    @Override
    @Transactional(readOnly = true) // Crucial if Trainer.user is lazy-loaded
    public List<Map<String, Object>> getTrainerPerformanceData() {
        return trainerRepository.findAll() // Fetches trainers, Trainer.user might be LAZY
                .stream()
                .map(trainer -> {
                    Map<String, Object> performance = new HashMap<>();
                    // Defensive null check for trainer.getUser()
                    if (trainer.getUser() != null) {
                        performance.put("trainerName", trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName());
                    } else {
                        performance.put("trainerName", "Unknown Trainer (ID: " + trainer.getId() + ")");
                    }

                    // This part re-queries all fitness classes and filters them, could be optimized
                    List<FitnessClass> trainerClasses = fitnessClassRepository.findAll().stream()
                            .filter(c -> c.getTrainer() != null && c.getTrainer().getId().equals(trainer.getId())) // Defensive null check
                            .collect(Collectors.toList());
                    performance.put("classesCount", trainerClasses.size());
                    performance.put("totalEnrollment", trainerClasses.stream()
                            .mapToInt(FitnessClass::getCurrentEnrollment).sum()); // Potential L.I.E. here
                    return performance;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true) // Crucial if Trainer.user is lazy-loaded
    public List<Map<String, Object>> getTrainerWorkloadData() {
        return trainerRepository.findAll()
                .stream()
                .map(trainer -> {
                    Map<String, Object> workload = new HashMap<>();
                    // Defensive null check for trainer.getUser() if you want the name here
                    if (trainer.getUser() != null) {
                        workload.put("trainerName", trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName());
                    } else {
                        workload.put("trainerName", "Trainer " + trainer.getId());
                    }

                    List<FitnessClass> trainerClasses = fitnessClassRepository.findAll().stream()
                            .filter(c -> c.getTrainer() != null && c.getTrainer().getId().equals(trainer.getId()))
                            .collect(Collectors.toList());
                    workload.put("activeClasses", trainerClasses.stream().filter(FitnessClass::isActive).count());
                    workload.put("totalClasses", trainerClasses.size());
                    return workload;
                })
                .collect(Collectors.toList());
    }

    // --- Recent Activity ---
    @Override
    @Transactional(readOnly = true) // Crucial for lazy-loaded member/fitnessClass
    public List<Map<String, Object>> getRecentBookingsData(int limit) {
        return classBookingRepository.findAll().stream()
                .sorted(Comparator.comparing(ClassBooking::getBookingDate, Comparator.nullsLast(Comparator.reverseOrder()))) // Assuming bookingDate exists
                .limit(limit)
                .map(booking -> {
                    Map<String, Object> bookingMap = new HashMap<>();
                    bookingMap.put("id", booking.getId());
                    // Defensive null checks for lazy-loaded associations
                    bookingMap.put("memberName", booking.getMember() != null && booking.getMember().getUser() != null ?
                    booking.getMember().getUser().getFirstName() + " " + booking.getMember().getUser().getLastName() : "N/A");
                    bookingMap.put("className", booking.getFitnessClass() != null ? booking.getFitnessClass().getName() : "N/A");
                    bookingMap.put("status", booking.getStatus());
                    return bookingMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true) // Crucial for lazy-loaded member
    public List<Map<String, Object>> getRecentPaymentsData(int limit) {
        return paymentRepository.findAll().stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(Comparator.reverseOrder()))) // Assuming paymentDate exists
                .limit(limit)
                .map(payment -> {
                    Map<String, Object> paymentMap = new HashMap<>();
                    paymentMap.put("id", payment.getId());
                    // Defensive null checks for lazy-loaded associations
                    paymentMap.put("memberName", payment.getMember() != null && payment.getMember().getUser() != null ?
                    payment.getMember().getUser().getFirstName() + " " + payment.getMember().getUser().getLastName() : "N/A");
                    paymentMap.put("amount", payment.getAmount());
                    paymentMap.put("status", payment.getStatus());
                    return paymentMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRecentMessagesData(int limit) {
        return contactMessageRepository.findAll().stream()
                .sorted(Comparator.comparing(ContactMessage::getSubmissionDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .map(message -> {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("id", message.getId());
                    messageMap.put("name", message.getName());
                    messageMap.put("email", message.getEmail());
                    messageMap.put("subject", message.getSubject());
                    messageMap.put("status", message.getStatus());
                    return messageMap;
                })
                .collect(Collectors.toList());
    }

}