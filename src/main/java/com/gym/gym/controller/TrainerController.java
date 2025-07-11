package com.gym.gym.controller;

import com.gym.gym.model.Trainer;
import com.gym.gym.service.TrainerService;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.DuplicateResourceException;
import com.gym.gym.exception.GlobalExceptionHandler.ErrorResponse;

import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gym.gym.dto.TrainerSearchDTO;

@RestController
@RequestMapping("/api/trainers")
@CrossOrigin(origins = "*")
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    /**
     * Create a new trainer
     * @param trainer Trainer details
     * @return Created trainer
     */
    @PostMapping
    public ResponseEntity<?> createTrainer(
            @Valid @RequestBody Trainer trainer,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            Trainer createdTrainer = trainerService.createTrainer(trainer);
            return ResponseEntity.ok(createdTrainer);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().body("Duplicate trainer: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating trainer: " + e.getMessage());
        }
    }

    /**
     * Get all trainers
     * @return List of all trainers
     */
    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    /**
     * Get trainer by ID
     * @param id Trainer ID
     * @return Trainer if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable Long id) {
        try {
            Trainer trainer = trainerService.getTrainerById(id);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update trainer
     * @param id Trainer ID
     * @param updatedTrainer Updated trainer details
     * @return Updated trainer
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody Trainer updatedTrainer,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            Trainer trainer = trainerService.updateTrainer(id, updatedTrainer);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating trainer: " + e.getMessage());
        }
    }

    /**
     * Delete trainer
     * @param id Trainer ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainer(@PathVariable Long id) {
        try {
            trainerService.deleteTrainer(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(
                    400,
                    "Bad Request",
                    "Cannot delete trainer: active bookings exist for associated classes.",
                    java.time.LocalDateTime.now()
                )
            );
            
        }
    }

    /**
     * Get available trainers
     * @return List of available trainers
     */
    @GetMapping("/available")
    public ResponseEntity<List<Trainer>> getAvailableTrainers() {
        List<Trainer> trainers = trainerService.getAvailableTrainers();
        return ResponseEntity.ok(trainers);
    }

    /**
     * Get trainers by specialization
     * @param specialization Specialization to search for
     * @return List of trainers with specialization
     */
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Trainer>> getTrainersBySpecialization(@PathVariable String specialization) {
        List<Trainer> trainers = trainerService.getTrainersBySpecialization(specialization);
        return ResponseEntity.ok(trainers);
    }

    /**
     * Get trainer by user email
     * @param email User email
     * @return Trainer if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getTrainerByEmail(@PathVariable String email) {
        try {
            Trainer trainer = trainerService.getTrainerByEmail(email);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update trainer availability
     * @param id Trainer ID
     * @param available Availability status
     * @return Updated trainer
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        try {
            Trainer trainer = trainerService.updateAvailability(id, available);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating availability: " + e.getMessage());
        }
    }

    /**
     * Add specialization to trainer
     * @param id Trainer ID
     * @param specialization Specialization to add
     * @return Updated trainer
     */
    @PutMapping("/{id}/specializations/add")
    public ResponseEntity<?> addSpecialization(
            @PathVariable Long id,
            @RequestParam String specialization) {
        try {
            Trainer trainer = trainerService.addSpecialization(id, specialization);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error adding specialization: " + e.getMessage());
        }
    }

    /**
     * Remove specialization from trainer
     * @param id Trainer ID
     * @param specialization Specialization to remove
     * @return Updated trainer
     */
    @PutMapping("/{id}/specializations/remove")
    public ResponseEntity<?> removeSpecialization(
            @PathVariable Long id,
            @RequestParam String specialization) {
        try {
            Trainer trainer = trainerService.removeSpecialization(id, specialization);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error removing specialization: " + e.getMessage());
        }
    }

    /**
     * Update trainer hourly rate
     * @param id Trainer ID
     * @param hourlyRate New hourly rate
     * @return Updated trainer
     */
    @PutMapping("/{id}/hourly-rate")
    public ResponseEntity<?> updateHourlyRate(
            @PathVariable Long id,
            @RequestParam double hourlyRate) {
        try {
            Trainer trainer = trainerService.updateHourlyRate(id, hourlyRate);
            return ResponseEntity.ok(trainer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating hourly rate: " + e.getMessage());
        }
    }

    /**
     * Search trainers by multiple criteria
     * @param specialization Optional specialization filter
     * @param available Optional availability filter
     * @param maxHourlyRate Optional maximum hourly rate filter
     * @return List of matching trainers
     */
    @GetMapping("/search")
    public ResponseEntity<List<TrainerSearchDTO>> searchTrainers(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double maxHourlyRate) {
        List<TrainerSearchDTO> trainers = trainerService.searchTrainers(specialization, available, maxHourlyRate);
        return ResponseEntity.ok(trainers);
    }
}