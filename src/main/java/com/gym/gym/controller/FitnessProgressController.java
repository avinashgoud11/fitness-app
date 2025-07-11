package com.gym.gym.controller;

import com.gym.gym.model.FitnessProgress;
import com.gym.gym.service.FitnessProgressService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class FitnessProgressController {

    @Autowired
    private FitnessProgressService fitnessProgressService;

    /**
     * Create a new fitness progress entry
     * @param fitnessProgress Progress details
     * @return Created progress entry
     */
    @PostMapping
    public ResponseEntity<?> createProgress(
            @Valid @RequestBody FitnessProgress fitnessProgress,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            FitnessProgress createdProgress = fitnessProgressService.createProgress(fitnessProgress);
            return ResponseEntity.ok(createdProgress);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating progress: " + e.getMessage());
        }
    }

    /**
     * Get all fitness progress entries
     * @return List of all progress entries
     */
    @GetMapping
    public ResponseEntity<List<FitnessProgress>> getAllProgress() {
        List<FitnessProgress> progress = fitnessProgressService.getAllProgress();
        return ResponseEntity.ok(progress);
    }

    /**
     * Get progress entry by ID
     * @param id Progress ID
     * @return Progress entry if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProgressById(@PathVariable Long id) {
        try {
            FitnessProgress progress = fitnessProgressService.getProgressById(id);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update fitness progress entry
     * @param id Progress ID
     * @param updatedProgress Updated progress details
     * @return Updated progress entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody FitnessProgress updatedProgress,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            FitnessProgress progress = fitnessProgressService.updateProgress(id, updatedProgress);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating progress: " + e.getMessage());
        }
    }

    /**
     * Delete fitness progress entry
     * @param id Progress ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        try {
            fitnessProgressService.deleteProgress(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get progress entries by member
     * @param memberId Member ID
     * @return List of member's progress entries
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FitnessProgress>> getProgressByMember(@PathVariable Long memberId) {
        List<FitnessProgress> progress = fitnessProgressService.getProgressByMember(memberId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get member's progress entries in date range
     * @param memberId Member ID
     * @param start Start date
     * @param end End date
     * @return List of member's progress entries in range
     */
    @GetMapping("/member/{memberId}/date-range")
    public ResponseEntity<List<FitnessProgress>> getMemberProgressByDateRange(
            @PathVariable Long memberId,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            List<FitnessProgress> progress = fitnessProgressService.getMemberProgressByDateRange(memberId, startDate, endDate);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get recent progress entries for member
     * @param memberId Member ID
     * @return List of recent progress entries
     */
    @GetMapping("/member/{memberId}/recent")
    public ResponseEntity<List<FitnessProgress>> getRecentProgressByMember(@PathVariable Long memberId) {
        List<FitnessProgress> progress = fitnessProgressService.getRecentProgressByMember(memberId);
        return ResponseEntity.ok(progress);
    }
} 