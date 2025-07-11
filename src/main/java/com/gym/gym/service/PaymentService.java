package com.gym.gym.service;

import com.gym.gym.model.Payment;
import com.gym.gym.model.Member;
import com.gym.gym.model.FitnessClass;
import com.gym.gym.model.ClassBooking;
import com.gym.gym.repository.PaymentRepository;
import com.gym.gym.repository.MemberRepository;
import com.gym.gym.repository.FitnessClassRepository;
import com.gym.gym.repository.ClassBookingRepository;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create a payment for a class booking
     * @param memberId Member ID
     * @param classId Fitness Class ID
     * @param amount Payment amount
     * @return Created payment
     */
    public Payment createPayment(Long memberId, Long classId, double amount) {
        // Validate member exists
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // Validate fitness class exists
        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found"));

        // Find or create class booking
        ClassBooking classBooking = classBookingRepository.findByMemberId(memberId)
                .stream()
                .filter(booking -> booking.getFitnessClass().getId().equals(classId))
                .findFirst()
                .orElseGet(() -> {
                    // Create booking if it doesn't exist
                    ClassBooking newBooking = new ClassBooking(LocalDateTime.now());
                    newBooking.setMember(member);
                    newBooking.setFitnessClass(fitnessClass);
                    newBooking.setStatus("CONFIRMED");
                    return classBookingRepository.save(newBooking);
                });

        // Check if payment already exists
        List<Payment> existingPayments = paymentRepository.findByMember_Id(memberId);
        boolean paymentExists = existingPayments.stream()
                .anyMatch(payment -> payment.getFitnessClass().getId().equals(classId));
        
        if (paymentExists) {
            throw new DuplicateResourceException("Payment already exists for this booking");
        }

        // Create payment
        Payment payment = new Payment(member, fitnessClass, classBooking, amount);
        payment = paymentRepository.save(payment);

        // Send booking confirmation emails
        try {
            emailService.sendBookingConfirmationEmail(payment);
            emailService.sendAdminBookingNotification(payment);
        } catch (Exception e) {
            // Log error but don't fail the payment creation
            System.err.println("Failed to send email notifications: " + e.getMessage());
        }

        return payment;
    }

    /**
     * Process payment at gym counter
     * @param paymentId Payment ID
     * @param paymentMethod Payment method
     * @param notes Additional notes
     * @return Updated payment
     */
    public Payment processPayment(Long paymentId, String paymentMethod, String notes) {
        Payment payment = getPaymentById(paymentId);

        if (!"PENDING".equals(payment.getStatus())) {
            throw new RuntimeException("Payment is not in pending status");
        }

        payment.setStatus("PAID");
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNotes(notes);

        payment = paymentRepository.save(payment);

        // Send payment confirmation emails
        try {
            emailService.sendPaymentConfirmationEmail(payment);
            emailService.sendAdminPaymentNotification(payment);
        } catch (Exception e) {
            // Log error but don't fail the payment processing
            System.err.println("Failed to send email notifications: " + e.getMessage());
        }

        return payment;
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payment by ID
     * @param id Payment ID
     * @return Payment if found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    /**
     * Get payments by member
     * @param memberId Member ID
     * @return List of member's payments
     */
    public List<Payment> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMember_Id(memberId);
    }

    /**
     * Get payments by status
     * @param status Payment status
     * @return List of payments with status
     */
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    /**
     * Get pending payments
     * @return List of pending payments
     */
    public List<Payment> getPendingPayments() {
        return getPaymentsByStatus("PENDING");
    }

    /**
     * Get paid payments
     * @return List of paid payments
     */
    public List<Payment> getPaidPayments() {
        return getPaymentsByStatus("PAID");
    }

    /**
     * Get payments by class
     * @param classId Fitness Class ID
     * @return List of payments for class
     */
    public List<Payment> getPaymentsByClass(Long classId) {
        return paymentRepository.findByFitnessClass_Id(classId);
    }

    /**
     * Get payments by payment method
     * @param paymentMethod Payment method
     * @return List of payments with method
     */
    public List<Payment> getPaymentsByMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    /**
     * Get payments in date range
     * @param start Start date
     * @param end End date
     * @return List of payments in range
     */
    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end);
    }

    /**
     * Get member's payments in date range
     * @param memberId Member ID
     * @param start Start date
     * @param end End date
     * @return List of member's payments in range
     */
    public List<Payment> getMemberPaymentsByDateRange(Long memberId, LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByMember_IdAndPaymentDateBetween(memberId, start, end);
    }

    /**
     * Cancel payment
     * @param paymentId Payment ID
     * @return Cancelled payment
     */
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus("CANCELLED");
        return paymentRepository.save(payment);
    }

    /**
     * Refund payment
     * @param paymentId Payment ID
     * @param notes Refund notes
     * @return Refunded payment
     */
    public Payment refundPayment(Long paymentId, String notes) {
        Payment payment = getPaymentById(paymentId);
        
        if (!"PAID".equals(payment.getStatus())) {
            throw new RuntimeException("Payment must be paid to be refunded");
        }

        payment.setStatus("REFUNDED");
        payment.setNotes(notes);
        return paymentRepository.save(payment);
    }

    /**
     * Get total revenue for a date range
     * @param start Start date
     * @param end End date
     * @return Total revenue
     */
    public double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        List<Payment> payments = getPaymentsByDateRange(start, end);
        return payments.stream()
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Get pending payments for a member
     * @param memberId Member ID
     * @return List of pending payments
     */
    public List<Payment> getPendingPaymentsByMember(Long memberId) {
        return paymentRepository.findByMember_IdAndStatus(memberId, "PENDING");
    }

    /**
     * Send payment reminders for pending payments
     */
    public void sendPaymentReminders() {
        List<Payment> pendingPayments = getPendingPayments();
        
        for (Payment payment : pendingPayments) {
            try {
                emailService.sendPaymentReminderEmail(payment);
            } catch (Exception e) {
                System.err.println("Failed to send reminder for payment " + payment.getId() + ": " + e.getMessage());
            }
        }
    }
} 