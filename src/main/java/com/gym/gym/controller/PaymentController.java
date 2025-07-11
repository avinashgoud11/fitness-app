package com.gym.gym.controller;

import com.gym.gym.model.Payment;
import com.gym.gym.service.PaymentService;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.DuplicateResourceException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create a payment for a class booking
     * @param memberId Member ID
     * @param classId Fitness Class ID
     * @param amount Payment amount
     * @return Created payment
     */
    @PostMapping("/book-session")
    public ResponseEntity<?> bookSession(
            @RequestParam Long memberId,
            @RequestParam Long classId,
            @RequestParam double amount) {
        try {
            Payment payment = paymentService.createPayment(memberId, classId, amount);
            return ResponseEntity.ok(payment);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().body("Duplicate booking: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }

    /**
     * Process payment at gym counter
     * @param paymentId Payment ID
     * @param paymentMethod Payment method
     * @param notes Additional notes
     * @return Updated payment
     */
    @PutMapping("/{paymentId}/process")
    public ResponseEntity<?> processPayment(
            @PathVariable Long paymentId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes) {
        try {
            Payment payment = paymentService.processPayment(paymentId, paymentMethod, notes);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error processing payment: " + e.getMessage());
        }
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payment by ID
     * @param id Payment ID
     * @return Payment if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get payments by member
     * @param memberId Member ID
     * @return List of member's payments
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Payment>> getPaymentsByMember(@PathVariable Long memberId) {
        List<Payment> payments = paymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by status
     * @param status Payment status
     * @return List of payments with status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get pending payments
     * @return List of pending payments
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * Get paid payments
     * @return List of paid payments
     */
    @GetMapping("/paid")
    public ResponseEntity<List<Payment>> getPaidPayments() {
        List<Payment> payments = paymentService.getPaidPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by class
     * @param classId Fitness Class ID
     * @return List of payments for class
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Payment>> getPaymentsByClass(@PathVariable Long classId) {
        List<Payment> payments = paymentService.getPaymentsByClass(classId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by payment method
     * @param paymentMethod Payment method
     * @return List of payments with method
     */
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        List<Payment> payments = paymentService.getPaymentsByMethod(paymentMethod);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments in date range
     * @param start Start date
     * @param end End date
     * @return List of payments in range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(start);
            LocalDateTime endDate = LocalDateTime.parse(end);
            List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get member's payments in date range
     * @param memberId Member ID
     * @param start Start date
     * @param end End date
     * @return List of member's payments in range
     */
    @GetMapping("/member/{memberId}/date-range")
    public ResponseEntity<List<Payment>> getMemberPaymentsByDateRange(
            @PathVariable Long memberId,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(start);
            LocalDateTime endDate = LocalDateTime.parse(end);
            List<Payment> payments = paymentService.getMemberPaymentsByDateRange(memberId, startDate, endDate);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancel payment
     * @param paymentId Payment ID
     * @return Cancelled payment
     */
    @PutMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.cancelPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error cancelling payment: " + e.getMessage());
        }
    }

    /**
     * Refund payment
     * @param paymentId Payment ID
     * @param notes Refund notes
     * @return Refunded payment
     */
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(
            @PathVariable Long paymentId,
            @RequestParam String notes) {
        try {
            Payment payment = paymentService.refundPayment(paymentId, notes);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error refunding payment: " + e.getMessage());
        }
    }

    /**
     * Get total revenue for a date range
     * @param start Start date
     * @param end End date
     * @return Total revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(start);
            LocalDateTime endDate = LocalDateTime.parse(end);
            double revenue = paymentService.getTotalRevenue(startDate, endDate);
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get pending payments for a member
     * @param memberId Member ID
     * @return List of pending payments
     */
    @GetMapping("/member/{memberId}/pending")
    public ResponseEntity<List<Payment>> getPendingPaymentsByMember(@PathVariable Long memberId) {
        List<Payment> payments = paymentService.getPendingPaymentsByMember(memberId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Send payment reminders for pending payments
     */
    @PostMapping("/send-reminders")
    public ResponseEntity<String> sendPaymentReminders() {
        try {
            paymentService.sendPaymentReminders();
            return ResponseEntity.ok("Payment reminders sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending reminders: " + e.getMessage());
        }
    }
} 