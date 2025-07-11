package com.gym.gym.controller;

import com.gym.gym.model.FitnessClass;
import com.gym.gym.service.FitnessClassService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class FitnessClassController {

    @Autowired
    private FitnessClassService fitnessClassService;

    /**
     * Create a new fitness class
     * @param fitnessClass Class details
     * @return Created class
     */
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody FitnessClass fitnessClass) {
        try {
            FitnessClass createdClass = fitnessClassService.createClass(fitnessClass);
            return ResponseEntity.ok(createdClass);
        } catch (RuntimeException e) {
        // Log the exception message for debugging
        System.err.println("Error creating class: " + e.getMessage());
        // Return the error message to Postman for better feedback
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Get all fitness classes
     * @return List of all classes
     */
    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAllClasses() {
        List<FitnessClass> classes = fitnessClassService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

    /**
     * Get class by ID
     * @param id Class ID
     * @return Class if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getClassById(@PathVariable Long id) {
        try {
            FitnessClass fitnessClass = fitnessClassService.getClassById(id);
            return ResponseEntity.ok(fitnessClass);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update fitness class
     * @param id Class ID
     * @param updatedClass Updated class details
     * @return Updated class
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(
        @PathVariable Long id,
        @Valid @RequestBody FitnessClass updatedClass,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            FitnessClass fitnessClass = fitnessClassService.updateClass(id, updatedClass);
            return ResponseEntity.ok(fitnessClass);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete fitness class
     * @param id Class ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        try {
            fitnessClassService.deleteClass(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get classes by trainer
     * @param trainerId Trainer ID
     * @return List of classes for trainer
     */
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<FitnessClass>> getClassesByTrainer(@PathVariable Long trainerId) {
        List<FitnessClass> classes = fitnessClassService.getClassesByTrainer(trainerId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by level
     * @param level Class level
     * @return List of classes for level
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<FitnessClass>> getClassesByLevel(@PathVariable String level) {
        List<FitnessClass> classes = fitnessClassService.getClassesByLevel(level);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get upcoming classes
     * @return List of upcoming classes
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<FitnessClass>> getUpcomingClasses() {
        List<FitnessClass> classes = fitnessClassService.getUpcomingClasses();
        return ResponseEntity.ok(classes);
    }

    /**
     * Get available classes (not full)
     * @return List of available classes
     */
    @GetMapping("/available")
    public ResponseEntity<List<FitnessClass>> getAvailableClasses() {
        List<FitnessClass> classes = fitnessClassService.getAvailableClasses();
        return ResponseEntity.ok(classes);
    }

    /**
     * Update class enrollment
     * @param id Class ID
     * @param increment true to increment, false to decrement
     * @return Updated class
     */
    @PutMapping("/{id}/enrollment")
    public ResponseEntity<?> updateEnrollment(
            @PathVariable Long id,
            @RequestParam boolean increment) {
        try {
            FitnessClass fitnessClass = fitnessClassService.updateEnrollment(id, increment);
            return ResponseEntity.ok(fitnessClass);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Search classes by keyword
     * @param keyword Search keyword
     * @return List of matching classes
     */
    @GetMapping("/search")
    public ResponseEntity<List<FitnessClass>> searchClasses(@RequestParam String keyword) {
        List<FitnessClass> classes = fitnessClassService.searchClasses(keyword);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by equipment
     * @param equipment Required equipment
     * @return List of classes requiring equipment
     */
    @GetMapping("/equipment")
    public ResponseEntity<List<FitnessClass>> getClassesByEquipment(@RequestParam String equipment) {
        List<FitnessClass> classes = fitnessClassService.getClassesByEquipment(equipment);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of classes within price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<FitnessClass>> getClassesByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<FitnessClass> classes = fitnessClassService.getClassesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by room
     * @param room Room name
     * @return List of classes in room
     */
    @GetMapping("/room/{room}")
    public ResponseEntity<List<FitnessClass>> getClassesByRoom(@PathVariable String room) {
        List<FitnessClass> classes = fitnessClassService.getClassesByRoom(room);
        return ResponseEntity.ok(classes);
    }
}