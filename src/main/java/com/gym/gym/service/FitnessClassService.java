package com.gym.gym.service;

import com.gym.gym.model.FitnessClass;
import com.gym.gym.model.ClassLevel; // Ensure this import is present
import com.gym.gym.model.Trainer;
import com.gym.gym.repository.FitnessClassRepository;
import com.gym.gym.repository.TrainerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class FitnessClassService {

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    /**
     * Create a new fitness class
     * @param fitnessClass Fitness class to create
     * @return Created fitness class
     */
    public FitnessClass createClass(FitnessClass fitnessClass) {
        validateClassTimes(fitnessClass);

        if (fitnessClass.getTrainer() == null || fitnessClass.getTrainer().getId() == null) {
            throw new RuntimeException("Trainer ID is required for creating a fitness class.");
        }

        Long trainerId = fitnessClass.getTrainer().getId();
        Trainer managedTrainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));

        fitnessClass.setTrainer(managedTrainer);

        return fitnessClassRepository.save(fitnessClass);
    }

    /**
     * Get all fitness classes
     * @return List of all fitness classes
     */
    public List<FitnessClass> getAllClasses() {
        return fitnessClassRepository.findAll();
    }

    /**
     * Get class by ID
     * @param id Class ID
     * @return Class if found
     */
    public FitnessClass getClassById(Long id) {
        return fitnessClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fitness class not found"));
    }

    /**
     * Update fitness class
     * @param id Class ID
     * @param updatedClass Updated class details
     * @return Updated class
     */
    public FitnessClass updateClass(Long id, FitnessClass updatedClass) {
        FitnessClass existingClass = getClassById(id);

        validateClassTimes(updatedClass);

        if (updatedClass.getTrainer() == null || updatedClass.getTrainer().getId() == null) {
            throw new RuntimeException("Trainer ID is required for updating a fitness class.");
        }

        Long trainerId = updatedClass.getTrainer().getId();
        Trainer managedTrainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));

        existingClass.setTrainer(managedTrainer);

        // Update fields
        existingClass.setName(updatedClass.getName());
        existingClass.setDescription(updatedClass.getDescription());
        existingClass.setStartTime(updatedClass.getStartTime());
        existingClass.setEndTime(updatedClass.getEndTime());
        existingClass.setMaxCapacity(updatedClass.getMaxCapacity());
        // existingClass.setTrainer(updatedClass.getTrainer()); // This line is correctly removed/commented
        existingClass.setRoom(updatedClass.getRoom());
        existingClass.setRequiredEquipment(updatedClass.getRequiredEquipment());
        existingClass.setPrice(updatedClass.getPrice());
        existingClass.setLevel(updatedClass.getLevel());

        return fitnessClassRepository.save(existingClass);
    }

    /**
     * Delete fitness class
     * @param id Class ID
     */
    public void deleteClass(Long id) {
        FitnessClass fitnessClass = getClassById(id);
        fitnessClassRepository.delete(fitnessClass);
    }

    /**
     * Get classes by trainer
     * @param trainerId Trainer ID
     * @return List of classes for trainer
     */
    public List<FitnessClass> getClassesByTrainer(Long trainerId) {
        return fitnessClassRepository.findByTrainerId(trainerId);
    }

    /**
     * Get classes by level
     * @param level Class level (as a String from the path variable)
     * @return List of classes for level
     */
    public List<FitnessClass> getClassesByLevel(String level) {
        try {
            ClassLevel classLevelEnum = ClassLevel.valueOf(level);
            return fitnessClassRepository.findByLevel(classLevelEnum); // Pass the enum name as String.
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid class level provided: " + level + ". Valid levels are: " + java.util.Arrays.toString(ClassLevel.values()), e);
        }
    }

    /**
     * Get upcoming classes
     * @return List of upcoming classes
     */
    public List<FitnessClass> getUpcomingClasses() {
        return fitnessClassRepository.findByStartTimeAfter(LocalDateTime.now());
    }

    /**
     * Get available classes (not full)
     * @return List of available classes
     */
    public List<FitnessClass> getAvailableClasses() {
        return fitnessClassRepository.findByCurrentEnrollmentLessThanMaxCapacity();
    }

    /**
     * Update class enrollment
     * @param id Class ID
     * @param increment true to increment, false to decrement
     * @return Updated class
     */
    public FitnessClass updateEnrollment(Long id, boolean increment) {
        FitnessClass fitnessClass = getClassById(id);

        if (increment) {
        if (fitnessClass.getCurrentEnrollment() >= fitnessClass.getMaxCapacity()) {
                throw new RuntimeException("Class is full");
            }
            fitnessClass.setCurrentEnrollment(fitnessClass.getCurrentEnrollment() + 1);
        } else {
            if (fitnessClass.getCurrentEnrollment() <= 0) {
                throw new RuntimeException("No enrollments to remove");
            }
            fitnessClass.setCurrentEnrollment(fitnessClass.getCurrentEnrollment() - 1);
        }

        return fitnessClassRepository.save(fitnessClass);
    }

    /**
     * Search classes by keyword
     * @param keyword Search keyword
     * @return List of matching classes
     */
    public List<FitnessClass> searchClasses(String keyword) {
        return fitnessClassRepository.searchByKeyword(keyword);
    }

    /**
     * Get classes by equipment
     * @param equipment Required equipment
     * @return List of classes requiring equipment
     */
    public List<FitnessClass> getClassesByEquipment(String equipment) {
        return fitnessClassRepository.findByRequiredEquipmentContaining(equipment);
    }

    /**
     * Validate class times
     * @param fitnessClass Class to validate
     */
    private void validateClassTimes(FitnessClass fitnessClass) {
        if (fitnessClass.getStartTime().isAfter(fitnessClass.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        if (fitnessClass.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot create class in the past");
        }
    }

    // This method should be completely removed, as its logic is now inline
    // private void validateTrainer(Long trainerId) {
    //     trainerRepository.findById(trainerId)
    //             .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));
    // }

    public List<FitnessClass> getClassesByRoom(String room) {
        return fitnessClassRepository.findByRoom(room);
    }

    public List<FitnessClass> getClassesByPriceRange(double minPrice, double maxPrice) {
        return fitnessClassRepository.findByPriceBetween(minPrice, maxPrice);
    }
}