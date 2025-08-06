package com.gym.gym.controller;

import com.gym.gym.model.ClassBooking;
import com.gym.gym.service.ClassBookingService;
import com.gym.gym.exception.ClassFullException;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class-bookings")
@CrossOrigin(origins = "*")
public class ClassBookingController {

    @Autowired
    private ClassBookingService classBookingService;

    /**
     * Create a new class booking
     * @param memberId Member ID
     * @param classId Fitness Class ID
     * @param bookingRequest A map containing memberId and classId
     * @return Created booking
     */
// Corrected Backend Code
@PostMapping
public ResponseEntity<?> createBooking(@RequestBody Map<String, Long> bookingRequest) {
    try {
        Long memberId = bookingRequest.get("memberId");
        Long classId = bookingRequest.get("classId");
        
        // Add null checks for memberId and classId if they are required
        if (memberId == null || classId == null) {
            return ResponseEntity.badRequest().body("Member ID and Class ID are required.");
        }

        ClassBooking booking = classBookingService.createBooking(memberId, classId);
        return ResponseEntity.ok(booking);
    } catch (ClassFullException e) {
        return ResponseEntity.badRequest().body("Class is full: " + e.getMessage());
    } catch (DuplicateResourceException e) {
        return ResponseEntity.badRequest().body("Duplicate booking: " + e.getMessage());
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Error creating booking: " + e.getMessage());
    }
}

    /**
     * Cancel a class booking
     * @param bookingId Booking ID
     * @return Cancelled booking
     */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            ClassBooking booking = classBookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error cancelling booking: " + e.getMessage());
        }
    }

    /**
     * Get all bookings for a member
     * @param memberId Member ID
     * @return List of member's bookings
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ClassBooking>> getMemberBookings(@PathVariable Long memberId) {
        List<ClassBooking> bookings = classBookingService.getMemberBookings(memberId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all bookings for a class
     * @param classId Fitness Class ID
     * @return List of class bookings
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassBooking>> getClassBookings(@PathVariable Long classId) {
        List<ClassBooking> bookings = classBookingService.getClassBookings(classId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by ID
     * @param bookingId Booking ID
     * @return Booking if found
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        try {
            ClassBooking booking = classBookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all active bookings
     * @return List of active bookings
     */
    @GetMapping("/active")
    public ResponseEntity<List<ClassBooking>> getActiveBookings() {
        List<ClassBooking> bookings = classBookingService.getActiveBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all cancelled bookings
     * @return List of cancelled bookings
     */
    @GetMapping("/cancelled")
    public ResponseEntity<List<ClassBooking>> getCancelledBookings() {
        List<ClassBooking> bookings = classBookingService.getCancelledBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Update booking status
     * @param bookingId Booking ID
     * @param status New status
     * @return Updated booking
     */
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam String status) {
        try {
            ClassBooking booking = classBookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating booking status: " + e.getMessage());
        }
    }

    /**
     * Delete a booking
     * @param bookingId Booking ID
     * @return Success response
     */
    @DeleteMapping("/{bookingId}")
public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
    try {
        return ResponseEntity.noContent().build(); // 204
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
}