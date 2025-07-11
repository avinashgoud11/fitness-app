package com.gym.gym.service;

import com.gym.gym.model.ClassBooking;
import com.gym.gym.model.FitnessClass;
import com.gym.gym.model.Member;
import com.gym.gym.repository.ClassBookingRepository;
import com.gym.gym.repository.FitnessClassRepository;
import com.gym.gym.repository.MemberRepository;
import com.gym.gym.exception.ClassFullException;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClassBookingService {

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    /**
     * Create a new class booking
     * @param memberId Member ID
     * @param classId Fitness Class ID
     * @return Created booking
     */
    public ClassBooking createBooking(Long memberId, Long classId) {
        // Check if member has access to classes
        if (!memberService.hasAccessToFeature(memberId, "classes")) {
            throw new RuntimeException("Member does not have access to classes");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Fitness class not found"));

        // Check if class is full
        if (isClassFull(classId)) {
            throw new ClassFullException("Class is full");
        }

        // Check if member already has a booking for this class
        if (hasExistingBooking(memberId, classId)) {
            throw new DuplicateResourceException("Member already has a booking for this class");
        }

        ClassBooking booking = new ClassBooking(LocalDateTime.now());
        booking.setMember(member);
        booking.setFitnessClass(fitnessClass);
        booking.setStatus("CONFIRMED");

        return classBookingRepository.save(booking);
    }

    /**
     * Cancel a class booking
     * @param bookingId Booking ID
     * @return Cancelled booking
     */
    public ClassBooking cancelBooking(Long bookingId) {
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if booking can be cancelled (e.g., not too close to class time)
        if (!canBeCancelled(booking)) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus("CANCELLED");
        return classBookingRepository.save(booking);
    }

    /**
     * Get all bookings for a member
     * @param memberId Member ID
     * @return List of member's bookings
     */
    public List<ClassBooking> getMemberBookings(Long memberId) {
        return classBookingRepository.findByMemberId(memberId);
    }

    /**
     * Get all bookings for a class
     * @param classId Fitness Class ID
     * @return List of class bookings
     */
    public List<ClassBooking> getClassBookings(Long classId) {
        return classBookingRepository.findByFitnessClassId(classId);
    }

    /**
     * Get booking by ID
     * @param bookingId Booking ID
     * @return Booking if found
     */
    public ClassBooking getBookingById(Long bookingId) {
        return classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    /**
     * Check if a class is full
     * @param classId Fitness Class ID
     * @return true if class is full
     */
    private boolean isClassFull(Long classId) {
        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Fitness class not found"));

        int currentBookings = classBookingRepository.countByFitnessClassIdAndStatus(classId, "CONFIRMED");
        return currentBookings >= fitnessClass.getMaxCapacity();
    }

    /**
     * Check if member has an existing booking for a class
     * @param memberId Member ID
     * @param classId Fitness Class ID
     * @return true if member has an existing booking
     */
    private boolean hasExistingBooking(Long memberId, Long classId) {
        return classBookingRepository.existsByMemberIdAndFitnessClassIdAndStatus(
                memberId, classId, "CONFIRMED");
    }

    /**
     * Check if a booking can be cancelled
     * @param booking Booking to check
     * @return true if booking can be cancelled
     */
    private boolean canBeCancelled(ClassBooking booking) {
        // Example: Can't cancel within 2 hours of class
        LocalDateTime classTime = booking.getFitnessClass().getStartTime();
        return LocalDateTime.now().plusHours(2).isBefore(classTime);
    }

    /**
     * Get all active bookings
     * @return List of active bookings
     */
    public List<ClassBooking> getActiveBookings() {
        return classBookingRepository.findByStatus("CONFIRMED");
    }

    /**
     * Get all cancelled bookings
     * @return List of cancelled bookings
     */
    public List<ClassBooking> getCancelledBookings() {
        return classBookingRepository.findByStatus("CANCELLED");
    }

    /**
     * Update booking status
     * @param bookingId Booking ID
     * @param status New status
     * @return Updated booking
     */
    public ClassBooking updateBookingStatus(Long bookingId, String status) {
        ClassBooking booking = getBookingById(bookingId);
        booking.setStatus(status);
        return classBookingRepository.save(booking);
    }
} 
