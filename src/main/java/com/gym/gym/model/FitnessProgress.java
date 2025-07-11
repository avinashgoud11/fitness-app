package com.gym.gym.model;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "fitness_progress")
@Data
@NoArgsConstructor
public class FitnessProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    private LocalDate date;

    private double weight;

    private double bodyFatPercentage;

    private double muscleMass;

    private double chestMeasurement;

    private double waistMeasurement;

    private double hipMeasurement;

    private double bicepMeasurement;

    private double thighMeasurement;

    @Size(max = 1000)
    private String notes;

    @Size(max = 500)
    private String achievements;

    @Size(max = 500)
    private String challenges;

    public FitnessProgress(String achievements) {
        this.achievements = achievements;
    }

    // Explicit getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getBodyFatPercentage() { return bodyFatPercentage; }
    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }

    public double getMuscleMass() { return muscleMass; }
    public void setMuscleMass(double muscleMass) { this.muscleMass = muscleMass; }

    public double getChestMeasurement() { return chestMeasurement; }
    public void setChestMeasurement(double chestMeasurement) { this.chestMeasurement = chestMeasurement; }

    public double getWaistMeasurement() { return waistMeasurement; }
    public void setWaistMeasurement(double waistMeasurement) { this.waistMeasurement = waistMeasurement; }

    public double getHipMeasurement() { return hipMeasurement; }
    public void setHipMeasurement(double hipMeasurement) { this.hipMeasurement = hipMeasurement; }

    public double getBicepMeasurement() { return bicepMeasurement; }
    public void setBicepMeasurement(double bicepMeasurement) { this.bicepMeasurement = bicepMeasurement; }

    public double getThighMeasurement() { return thighMeasurement; }
    public void setThighMeasurement(double thighMeasurement) { this.thighMeasurement = thighMeasurement; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public String getChallenges() { return challenges; }
    public void setChallenges(String challenges) { this.challenges = challenges; }
} 
