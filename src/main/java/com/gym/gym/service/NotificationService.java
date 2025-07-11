package com.gym.gym.service;

import com.gym.gym.model.*;
import com.gym.gym.repository.*;
import com.gym.gym.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Send class reminder notifications
     * @param classId Fitness class ID
     * @return Number of notifications sent
     */
    public int sendClassReminders(Long classId) {
        try {
            FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found"));

            List<ClassBooking> bookings = classBookingRepository.findByFitnessClassId(classId).stream()
                    .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                    .collect(Collectors.toList());
            
            int sentCount = 0;
            for (ClassBooking booking : bookings) {
                try {
                    sendClassReminderEmail(booking);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send reminder to " + booking.getMember().getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send class reminders: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send payment reminder notifications
     * @return Number of notifications sent
     */
    public int sendPaymentReminders() {
        try {
            List<Payment> pendingPayments = paymentRepository.findByStatus("PENDING");
            
            int sentCount = 0;
            for (Payment payment : pendingPayments) {
                try {
                    sendPaymentReminderEmail(payment);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send payment reminder to " + payment.getMember().getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send payment reminders: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send membership expiry notifications
     * @param daysBeforeExpiry Days before expiry to send notification
     * @return Number of notifications sent
     */
    public int sendMembershipExpiryReminders(int daysBeforeExpiry) {
        try {
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(daysBeforeExpiry);
            List<Member> expiringMembers = memberRepository.findAll().stream()
                    .filter(member -> member.getMembershipEndDate() != null && member.getMembershipEndDate().isBefore(expiryDate))
                    .collect(Collectors.toList());
            
            int sentCount = 0;
            for (Member member : expiringMembers) {
                try {
                    sendMembershipExpiryEmail(member);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send expiry reminder to " + member.getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send membership expiry reminders: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send welcome notification to new member
     * @param memberId Member ID
     * @return Success status
     */
    public boolean sendWelcomeNotification(Long memberId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
            
            sendWelcomeEmail(member);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send welcome notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send class cancellation notification
     * @param classId Fitness class ID
     * @param reason Cancellation reason
     * @return Number of notifications sent
     */
    public int sendClassCancellationNotification(Long classId, String reason) {
        try {
            FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found"));

            List<ClassBooking> bookings = classBookingRepository.findByFitnessClassId(classId).stream()
                    .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                    .collect(Collectors.toList());
            
            int sentCount = 0;
            for (ClassBooking booking : bookings) {
                try {
                    sendClassCancellationEmail(booking, reason);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send cancellation notification to " + booking.getMember().getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send class cancellation notifications: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send class schedule change notification
     * @param classId Fitness class ID
     * @param oldTime Old class time
     * @param newTime New class time
     * @return Number of notifications sent
     */
    public int sendClassScheduleChangeNotification(Long classId, LocalDateTime oldTime, LocalDateTime newTime) {
        try {
            FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found"));

            List<ClassBooking> bookings = classBookingRepository.findByFitnessClassId(classId).stream()
                    .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                    .collect(Collectors.toList());
            
            int sentCount = 0;
            for (ClassBooking booking : bookings) {
                try {
                    sendClassScheduleChangeEmail(booking, oldTime, newTime);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send schedule change notification to " + booking.getMember().getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send class schedule change notifications: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send bulk notification to all members
     * @param subject Email subject
     * @param message Email message
     * @return Number of notifications sent
     */
    public int sendBulkNotification(String subject, String message) {
        try {
            List<Member> allMembers = memberRepository.findAll();
            
            int sentCount = 0;
            for (Member member : allMembers) {
                try {
                    sendBulkEmail(member, subject, message);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send bulk notification to " + member.getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send bulk notifications: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send notification to specific member
     * @param memberId Member ID
     * @param subject Email subject
     * @param message Email message
     * @return Success status
     */
    public boolean sendMemberNotification(Long memberId, String subject, String message) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
            
            sendMemberEmail(member, subject, message);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send member notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send notification to members by membership type
     * @param membershipType Membership type
     * @param subject Email subject
     * @param message Email message
     * @return Number of notifications sent
     */
    public int sendNotificationByMembershipType(MembershipType membershipType, String subject, String message) {
        try {
            List<Member> members = memberRepository.findByMembershipType(membershipType);
            
            int sentCount = 0;
            for (Member member : members) {
                try {
                    sendMemberEmail(member, subject, message);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send notification to " + member.getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send notifications by membership type: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Send notification to inactive members
     * @param subject Email subject
     * @param message Email message
     * @return Number of notifications sent
     */
    public int sendInactiveMemberNotification(String subject, String message) {
        try {
            List<Member> inactiveMembers = memberRepository.findAll().stream()
                    .filter(member -> !member.isActive())
                    .collect(Collectors.toList());
            
            int sentCount = 0;
            for (Member member : inactiveMembers) {
                try {
                    sendMemberEmail(member, subject, message);
                    sentCount++;
                } catch (Exception e) {
                    System.err.println("Failed to send notification to inactive member " + member.getUser().getEmail() + ": " + e.getMessage());
                }
            }
            
            return sentCount;
        } catch (Exception e) {
            System.err.println("Failed to send inactive member notifications: " + e.getMessage());
            return 0;
        }
    }

    // Email sending methods

    private void sendClassReminderEmail(ClassBooking booking) {
        String subject = "Class Reminder: " + booking.getFitnessClass().getName();
        String message = """
            Hi %s,
            
            This is a reminder that you have a class tomorrow:
            
            Class: %s
            Time: %s
            Trainer: %s
            Room: %s
            
            Please arrive 10 minutes before the class starts.
            
            Best regards,
            Gym Management Team""".formatted(
                booking.getMember().getUser().getFirstName(),
                booking.getFitnessClass().getName(),
                booking.getFitnessClass().getStartTime(),
                booking.getFitnessClass().getTrainer().getUser().getFirstName() + " " + booking.getFitnessClass().getTrainer().getUser().getLastName(),
                booking.getFitnessClass().getRoom()
            );
        
        emailService.sendEmail(booking.getMember().getUser().getEmail(), subject, message);
    }

    private void sendPaymentReminderEmail(Payment payment) {
        String subject = "Payment Reminder";
        String message = """
            Hi %s,
            
            This is a reminder that you have a pending payment:
            
            Class: %s
            Amount: $%.2f
            Due Date: %s
            
            Please visit the gym to complete your payment.
            
            Best regards,
            Gym Management Team""".formatted(
                payment.getMember().getUser().getFirstName(),
                payment.getFitnessClass().getName(),
                payment.getAmount(),
                payment.getBookingDate().plusDays(7)
            );
        
        emailService.sendEmail(payment.getMember().getUser().getEmail(), subject, message);
    }

    private void sendMembershipExpiryEmail(Member member) {
        String subject = "Membership Expiry Reminder";
        String message = """
            Hi %s,
            
            Your membership will expire on %s.
            
            To continue enjoying our services, please renew your membership.
            
            Current Membership: %s
            Expiry Date: %s
            
            Contact us for renewal options.
            
            Best regards,
            Gym Management Team""".formatted(
                member.getUser().getFirstName(),
                member.getMembershipEndDate(),
                member.getMembershipType(),
                member.getMembershipEndDate()
            );
        
        emailService.sendEmail(member.getUser().getEmail(), subject, message);
    }

    private void sendWelcomeEmail(Member member) {
        String subject = "Welcome to Our Gym!";
        String message = """
            Hi %s,
            
            Welcome to our gym family!
            
            We're excited to have you as a member.
            
            Your Membership Details:
            Type: %s
            Start Date: %s
            Expiry Date: %s
            
            Please visit us to complete your registration and get your membership card.
            
            Best regards,
            Gym Management Team""".formatted(
                member.getUser().getFirstName(),
                member.getMembershipType(),
                member.getMembershipStartDate(),
                member.getMembershipEndDate()
            );
        
        emailService.sendEmail(member.getUser().getEmail(), subject, message);
    }

    private void sendClassCancellationEmail(ClassBooking booking, String reason) {
        String subject = "Class Cancelled: " + booking.getFitnessClass().getName();
        String message = """
            Hi %s,
            
            Unfortunately, the following class has been cancelled:
            
            Class: %s
            Date: %s
            Time: %s
            Reason: %s
            
            We apologize for any inconvenience. Please check our schedule for alternative classes.
            
            Best regards,
            Gym Management Team""".formatted(
                booking.getMember().getUser().getFirstName(),
                booking.getFitnessClass().getName(),
                booking.getFitnessClass().getStartTime().toLocalDate(),
                booking.getFitnessClass().getStartTime().toLocalTime(),
                reason
            );
        
        emailService.sendEmail(booking.getMember().getUser().getEmail(), subject, message);
    }

    private void sendClassScheduleChangeEmail(ClassBooking booking, LocalDateTime oldTime, LocalDateTime newTime) {
        String subject = "Class Schedule Change: " + booking.getFitnessClass().getName();
        String message = """
            Hi %s,
            
            The schedule for the following class has been changed:
            
            Class: %s
            Old Time: %s
            New Time: %s
            
            Please update your calendar accordingly.
            
            Best regards,
            Gym Management Team""".formatted(
                booking.getMember().getUser().getFirstName(),
                booking.getFitnessClass().getName(),
                oldTime,
                newTime
            );
        
        emailService.sendEmail(booking.getMember().getUser().getEmail(), subject, message);
    }

    private void sendBulkEmail(Member member, String subject, String message) {
        String personalizedMessage = """
            Hi %s,
            
            %s
            
            Best regards,
            Gym Management Team""".formatted(
                member.getUser().getFirstName(),
                message
            );
        
        emailService.sendEmail(member.getUser().getEmail(), subject, personalizedMessage);
    }

    private void sendMemberEmail(Member member, String subject, String message) {
        String personalizedMessage = """
            Hi %s,
            
            %s
            
            Best regards,
            Gym Management Team""".formatted(
                member.getUser().getFirstName(),
                message
            );
        
        emailService.sendEmail(member.getUser().getEmail(), subject, personalizedMessage);
    }

    /**
     * Get notification statistics
     * @return Notification statistics
     */
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeMembers", memberRepository.findByActiveTrue().size());
        stats.put("pendingPayments", paymentRepository.findByStatus("PENDING").size());
        stats.put("upcomingClasses", fitnessClassRepository.findAll().stream()
                .filter(c -> c.getStartTime().isAfter(LocalDateTime.now()))
                .count());
        
        return stats;
    }
} 