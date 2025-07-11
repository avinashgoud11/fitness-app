package com.gym.gym.dto;

import java.util.Set;
import java.util.HashSet;

import com.gym.gym.model.Trainer;

public class TrainerSearchDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> specializations; // This will hold the actual data
    private Boolean available;
    private Double hourlyRate;
    // ... add other fields you want to expose from Trainer or User

    // Constructors (e.g., default, and one for mapping from Trainer)
    public TrainerSearchDTO() {}

    public TrainerSearchDTO(Trainer trainer) {
        this.id = trainer.getId();
        // Assuming Trainer has a 'user' field with firstName, lastName, email
        if (trainer.getUser() != null) {
            this.firstName = trainer.getUser().getFirstName();
            this.lastName = trainer.getUser().getLastName();
            this.email = trainer.getUser().getEmail();
        }
        // IMPORTANT: Initialize the lazy collection while the session is open
        this.specializations = new HashSet<>(trainer.getSpecializations());
        this.available = trainer.getAvailable();
        this.hourlyRate = trainer.getHourlyRate();
        // ... map other fields
    }

    // Getters and Setters
    // ... (generate for all fields)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<String> getSpecializations() { return specializations; }
    public void setSpecializations(Set<String> specializations) { this.specializations = specializations; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
}


