package com.gym.gym.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GymManagementController{

    private String gymName;

    // Default constructor is intentionally left empty as no initialization is required.
    public GymManagementController() {
        // Uncomment the following line if this constructor should not be used.
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    @GetMapping("/hello")
    public String greet() {
        return "Welcome to the Gym Management System!";
    }

    // Example: Add more endpoints
    @GetMapping("/status")
    public String status() {
        return "Gym system is running fine!";
    }
}
