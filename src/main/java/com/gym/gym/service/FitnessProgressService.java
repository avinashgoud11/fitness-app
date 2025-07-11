package com.gym.gym.service;
import com.gym.gym.model.FitnessProgress;
import com.gym.gym.model.Member;
import com.gym.gym.repository.FitnessProgressRepository;
import com.gym.gym.repository.MemberRepository;
import com.gym.gym.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FitnessProgressService {

    @Autowired
    private FitnessProgressRepository fitnessProgressRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Create a new fitness progress entry
     * @param fitnessProgress Progress details
     * @return Created progress entry
     */
    public FitnessProgress createProgress(FitnessProgress fitnessProgress) {
        // Validate member exists
        Member member = memberRepository.findById(fitnessProgress.getMember().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        fitnessProgress.setMember(member);
        return fitnessProgressRepository.save(fitnessProgress);
    }

    /**
     * Get all fitness progress entries
     * @return List of all progress entries
     */
    public List<FitnessProgress> getAllProgress() {
        return fitnessProgressRepository.findAll();
    }

    /**
     * Get progress entry by ID
     * @param id Progress ID
     * @return Progress entry if found
     */
    public FitnessProgress getProgressById(Long id) {
        return fitnessProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fitness progress not found"));
    }

    /**
     * Update fitness progress entry
     * @param id Progress ID
     * @param updatedProgress Updated progress details
     * @return Updated progress entry
     */
    public FitnessProgress updateProgress(Long id, FitnessProgress updatedProgress) {
        FitnessProgress existingProgress = getProgressById(id);

        // Update fields
        existingProgress.setDate(updatedProgress.getDate());
        existingProgress.setWeight(updatedProgress.getWeight());
        existingProgress.setBodyFatPercentage(updatedProgress.getBodyFatPercentage());
        existingProgress.setMuscleMass(updatedProgress.getMuscleMass());
        existingProgress.setChestMeasurement(updatedProgress.getChestMeasurement());
        existingProgress.setWaistMeasurement(updatedProgress.getWaistMeasurement());
        existingProgress.setHipMeasurement(updatedProgress.getHipMeasurement());
        existingProgress.setBicepMeasurement(updatedProgress.getBicepMeasurement());
        existingProgress.setThighMeasurement(updatedProgress.getThighMeasurement());
        existingProgress.setNotes(updatedProgress.getNotes());
        existingProgress.setAchievements(updatedProgress.getAchievements());
        existingProgress.setChallenges(updatedProgress.getChallenges());

        return fitnessProgressRepository.save(existingProgress);
    }

    /**
     * Delete fitness progress entry
     * @param id Progress ID
     */
    public void deleteProgress(Long id) {
        FitnessProgress progress = getProgressById(id);
        fitnessProgressRepository.delete(progress);
    }

    /**
     * Get progress entries by member
     * @param memberId Member ID
     * @return List of member's progress entries
     */
    public List<FitnessProgress> getProgressByMember(Long memberId) {
        return fitnessProgressRepository.findByMember_Id(memberId);
    }

    /**
     * Get member's progress entries in date range
     * @param memberId Member ID
     * @param start Start date
     * @param end End date
     * @return List of member's progress entries in range
     */
    public List<FitnessProgress> getMemberProgressByDateRange(Long memberId, LocalDate start, LocalDate end) {
        return fitnessProgressRepository.findByMember_IdAndDateBetween(memberId, start, end);
    }

    /**
     * Get recent progress entries for member
     * @param memberId Member ID
     * @return List of recent progress entries
     */
    public List<FitnessProgress> getRecentProgressByMember(Long memberId) {
        return fitnessProgressRepository.findByMember_IdOrderByDateDesc(memberId);
    }
} 