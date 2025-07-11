package com.gym.gym.service;

import com.gym.gym.model.Payment;
import com.gym.gym.model.Member;
import com.gym.gym.model.FitnessClass;
import com.gym.gym.model.User;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    /**
     * Send booking confirmation email to member
     * @param payment Payment details
     */
    public void sendBookingConfirmationEmail(Payment payment) {
        Member member = payment.getMember();
        FitnessClass fitnessClass = payment.getFitnessClass();
        User user = member.getUser();

        String subject = "Booking Confirmation - " + fitnessClass.getName();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Your booking has been confirmed!\n\n" +
            "Class Details:\n" +
            "- Class: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Trainer: %s\n" +
            "- Room: %s\n\n" +
            "Payment Details:\n" +
            "- Amount: $%.2f\n" +
            "- Status: %s\n\n" +
            "Please pay at the gym counter before the class.\n\n" +
            "Thank you for choosing our gym!\n\n" +
            "Best regards,\n" +
            "Gym Management Team",
            user.getFirstName(),
            user.getLastName(),
            fitnessClass.getName(),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            fitnessClass.getTrainer() != null ? fitnessClass.getTrainer().getUser().getFirstName() + " " + fitnessClass.getTrainer().getUser().getLastName() : "TBD",
            fitnessClass.getRoom(),
            payment.getAmount(),
            payment.getStatus()
        );

        logEmail(user.getEmail(), subject, message);
    }

    /**
     * Send payment confirmation email to member
     * @param payment Payment details
     */
    public void sendPaymentConfirmationEmail(Payment payment) {
        Member member = payment.getMember();
        FitnessClass fitnessClass = payment.getFitnessClass();
        User user = member.getUser();

        String subject = "Payment Confirmation - " + fitnessClass.getName();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Your payment has been received!\n\n" +
            "Class Details:\n" +
            "- Class: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Trainer: %s\n" +
            "- Room: %s\n\n" +
            "Payment Details:\n" +
            "- Amount: $%.2f\n" +
            "- Method: %s\n" +
            "- Date: %s\n" +
            "- Status: %s\n\n" +
            "Your booking is now confirmed. See you at the class!\n\n" +
            "Thank you!\n\n" +
            "Best regards,\n" +
            "Gym Management Team",
            user.getFirstName(),
            user.getLastName(),
            fitnessClass.getName(),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            fitnessClass.getTrainer() != null ? fitnessClass.getTrainer().getUser().getFirstName() + " " + fitnessClass.getTrainer().getUser().getLastName() : "TBD",
            fitnessClass.getRoom(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            payment.getStatus()
        );

        logEmail(user.getEmail(), subject, message);
    }

    /**
     * Send booking notification to admin
     * @param payment Payment details
     */
    public void sendAdminBookingNotification(Payment payment) {
        Member member = payment.getMember();
        FitnessClass fitnessClass = payment.getFitnessClass();
        User user = member.getUser();

        String subject = "New Booking - " + fitnessClass.getName();
        String message = String.format(
            "New booking received:\n\n" +
            "Member Details:\n" +
            "- Name: %s %s\n" +
            "- Email: %s\n" +
            "- Phone: %s\n\n" +
            "Class Details:\n" +
            "- Class: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Trainer: %s\n" +
            "- Room: %s\n\n" +
            "Payment Details:\n" +
            "- Amount: $%.2f\n" +
            "- Status: %s\n" +
            "- Booking Date: %s\n\n" +
            "Please process the payment at the counter.",
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            member.getPhoneNumber(),
            fitnessClass.getName(),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            fitnessClass.getTrainer() != null ? fitnessClass.getTrainer().getUser().getFirstName() + " " + fitnessClass.getTrainer().getUser().getLastName() : "TBD",
            fitnessClass.getRoom(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        logEmail("admin@gym.com", subject, message);
    }

    /**
     * Send payment notification to admin
     * @param payment Payment details
     */
    public void sendAdminPaymentNotification(Payment payment) {
        Member member = payment.getMember();
        FitnessClass fitnessClass = payment.getFitnessClass();
        User user = member.getUser();

        String subject = "Payment Received - " + fitnessClass.getName();
        String message = String.format(
            "Payment received:\n\n" +
            "Member Details:\n" +
            "- Name: %s %s\n" +
            "- Email: %s\n" +
            "- Phone: %s\n\n" +
            "Class Details:\n" +
            "- Class: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Trainer: %s\n" +
            "- Room: %s\n\n" +
            "Payment Details:\n" +
            "- Amount: $%.2f\n" +
            "- Method: %s\n" +
            "- Date: %s\n" +
            "- Status: %s\n\n" +
            "Payment has been processed successfully.",
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            member.getPhoneNumber(),
            fitnessClass.getName(),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            fitnessClass.getTrainer() != null ? fitnessClass.getTrainer().getUser().getFirstName() + " " + fitnessClass.getTrainer().getUser().getLastName() : "TBD",
            fitnessClass.getRoom(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            payment.getStatus()
        );

        logEmail("admin@gym.com", subject, message);
    }

    /**
     * Send reminder email for pending payments
     * @param payment Payment details
     */
    public void sendPaymentReminderEmail(Payment payment) {
        Member member = payment.getMember();
        FitnessClass fitnessClass = payment.getFitnessClass();
        User user = member.getUser();

        String subject = "Payment Reminder - " + fitnessClass.getName();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "This is a reminder that your payment is pending for the following class:\n\n" +
            "Class Details:\n" +
            "- Class: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Trainer: %s\n" +
            "- Room: %s\n\n" +
            "Payment Details:\n" +
            "- Amount: $%.2f\n" +
            "- Status: %s\n\n" +
            "Please visit the gym counter to complete your payment before the class.\n\n" +
            "Thank you!\n\n" +
            "Best regards,\n" +
            "Gym Management Team",
            user.getFirstName(),
            user.getLastName(),
            fitnessClass.getName(),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            fitnessClass.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            fitnessClass.getTrainer() != null ? fitnessClass.getTrainer().getUser().getFirstName() + " " + fitnessClass.getTrainer().getUser().getLastName() : "TBD",
            fitnessClass.getRoom(),
            payment.getAmount(),
            payment.getStatus()
        );

        logEmail(user.getEmail(), subject, message);
    }

    /**
     * Send a generic email (logs to console for now)
     * @param to Recipient email
     * @param subject Email subject
     * @param message Email message
     */
    public void sendEmail(String to, String subject, String message) {
        logEmail(to, subject, message);
    }

    /**
     * Log email instead of sending (for development/testing)
     * @param to Recipient email
     * @param subject Email subject
     * @param message Email message
     */
    private void logEmail(String to, String subject, String message) {
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Message:");
        System.out.println(message);
        System.out.println("==================");
    }
} 