package com.gym.gym.service;

import com.gym.gym.model.Trainer;
import com.gym.gym.model.User;
import com.gym.gym.repository.TrainerRepository;
import com.gym.gym.repository.UserRepository;
import com.gym.gym.repository.FitnessClassRepository;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.dto.TrainerSearchDTO;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // <--- ADD THIS IMPORT
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.gym.gym.model.FitnessClass;

@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired // <--- ADD THIS AUTOWIRED ANNOTATION FOR PasswordEncoder
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    /**
     * Create a new trainer
     * @param trainer Trainer details
     * @return Created trainer
     */
    public Trainer createTrainer(Trainer trainer) {
        User userToCreate = trainer.getUser();

        // **IMPORTANT: Handle new User creation and password encoding**
        // Check if a user with the given username or email already exists
        userRepository.findByUsername(userToCreate.getUsername()).ifPresent(u -> {
            throw new DuplicateResourceException("User with username " + u.getUsername() + " already exists.");
        });
        userRepository.findByEmail(userToCreate.getEmail()).ifPresent(u -> {
            throw new DuplicateResourceException("User with email " + u.getEmail() + " already exists.");
        });

        // Encode the password before saving the new user
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        // Ensure 'enabled' is set if not already handled by default or during user creation
        // userToCreate.setEnabled(true); // Example, if you need to explicitly set it

        // Save the User first to get its generated ID
        User savedUser = userRepository.save(userToCreate);

        // Now, check if a trainer already exists for this newly saved user's email
        // (This check assumes one user can only be one type of role or only be associated with one trainer entity)
        if (trainerRepository.findByUser_Email(savedUser.getEmail()).size() > 0) {
            // If a trainer already exists for this user, revert the user creation (optional but good practice)
            userRepository.delete(savedUser); // Clean up the user if trainer creation cannot proceed
            throw new DuplicateResourceException("A trainer profile already exists for this user's email.");
        }

        // Set the savedUser (which now has an ID) to the trainer object
        trainer.setUser(savedUser);

        // Save the trainer
        return trainerRepository.save(trainer);
    }

    // ... rest of your TrainerService methods (unchanged)

    /**
     * Get all trainers
     * @return List of all trainers
     */
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    /**
     * Get trainer by ID
     * @param id Trainer ID
     * @return Trainer if found
     */
    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
    }

    /**
     * Update trainer
     * @param id Trainer ID
     * @param updatedTrainer Updated trainer details
     * @return Updated trainer
     */
    public Trainer updateTrainer(Long id, Trainer updatedTrainer) {
        Trainer existingTrainer = getTrainerById(id);

        // Update fields
        existingTrainer.setDateOfBirth(updatedTrainer.getDateOfBirth());
        existingTrainer.setGender(updatedTrainer.getGender());
        existingTrainer.setPhoneNumber(updatedTrainer.getPhoneNumber());
        existingTrainer.setAddress(updatedTrainer.getAddress());
        // Handle specializations: clear existing and add new ones or update carefully
        if (updatedTrainer.getSpecializations() != null) {
            existingTrainer.setSpecializations(updatedTrainer.getSpecializations());
        }
        existingTrainer.setCertifications(updatedTrainer.getCertifications());
        existingTrainer.setExperience(updatedTrainer.getExperience());
        existingTrainer.setHourlyRate(updatedTrainer.getHourlyRate());
        existingTrainer.setAvailable(updatedTrainer.getAvailable());
        existingTrainer.setBio(updatedTrainer.getBio());

        return trainerRepository.save(existingTrainer);
    }

/**
 * Delete trainer
 * @param id Trainer ID
 */
// In TrainerService.java
@Transactional
public void deleteTrainer(Long id) {
    Trainer trainer = trainerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

    // Option 1: Prevent deletion if classes have enrollments
    for (FitnessClass fitnessClass : trainer.getFitnessClasses()) {
        if (fitnessClass.getEnrollments() != null && !fitnessClass.getEnrollments().isEmpty()) {
            throw new IllegalStateException("Cannot delete trainer because one or more associated fitness classes have active enrollments. Please delete classes/enrollments first.");
        }
        // You might also want to delete equipment associated with the class here
        // if not handled by CascadeType.ALL or orphanRemoval
    }

    // If all checks pass, proceed with deletion
    trainerRepository.delete(trainer);
}
    /**
     * Get available trainers
     * @return List of available trainers
     */
    public List<Trainer> getAvailableTrainers() {
        return trainerRepository.findByAvailableTrue();
    }

    /**
     * Get trainers by specialization
     * @param specialization Specialization to search for
     * @return List of trainers with specialization
     */
    public List<Trainer> getTrainersBySpecialization(String specialization) {
        return trainerRepository.findBySpecializationsContaining(specialization);
    }

    /**
     * Get trainer by user email
     * @param email User email
     * @return Trainer if found
     */
    public Trainer getTrainerByEmail(String email) {
        List<Trainer> trainers = trainerRepository.findByUser_Email(email);
        if (trainers.isEmpty()) {
            throw new ResourceNotFoundException("Trainer not found for email: " + email);
        }
        return trainers.get(0);
    }

    /**
     * Update trainer availability
     * @param id Trainer ID
     * @param available Availability status
     * @return Updated trainer
     */
    public Trainer updateAvailability(Long id, boolean available) {
        Trainer trainer = getTrainerById(id);
        trainer.setAvailable(available);
        return trainerRepository.save(trainer);
    }

    /**
     * Add specialization to trainer
     * @param id Trainer ID
     * @param specialization Specialization to add
     * @return Updated trainer
     */
    public Trainer addSpecialization(Long id, String specialization) {
        Trainer trainer = getTrainerById(id);
        Set<String> specializations = trainer.getSpecializations();
        if (specializations == null) { // Handle null set if not initialized by Lombok or constructor
            trainer.setSpecializations(Set.of(specialization));
        } else {
            specializations.add(specialization);
            trainer.setSpecializations(specializations);
        }
        return trainerRepository.save(trainer);
    }

    /**
     * Remove specialization from trainer
     * @param id Trainer ID
     * @param specialization Specialization to remove
     * @return Updated trainer
     */
    public Trainer removeSpecialization(Long id, String specialization) {
        Trainer trainer = getTrainerById(id);
        Set<String> specializations = trainer.getSpecializations();
        if (specializations != null) {
            specializations.remove(specialization);
            trainer.setSpecializations(specializations);
        }
        return trainerRepository.save(trainer);
    }

    /**
     * Update trainer hourly rate
     * @param id Trainer ID
     * @param hourlyRate New hourly rate
     * @return Updated trainer
     */
    public Trainer updateHourlyRate(Long id, double hourlyRate) {
        Trainer trainer = getTrainerById(id);
        trainer.setHourlyRate(hourlyRate);
        return trainerRepository.save(trainer);
    }

    /**
     * Search trainers by multiple criteria
     * @param specialization Optional specialization filter
     * @param available Optional availability filter
     * @param maxHourlyRate Optional maximum hourly rate filter
     * @return List of matching trainers
     */
    @Transactional(readOnly = true) // Ensure this method is transactional for session management
    public List<TrainerSearchDTO> searchTrainers(String specialization, Boolean available, Double maxHourlyRate) {
        // Option 1: Fetch all and filter in memory (if dataset is small, or for initial testing)
        // As observed in your logs, your current implementation might be doing this.
        List<Trainer> allTrainers = trainerRepository.findAll(); // This fetches basic Trainer data

        List<TrainerSearchDTO> results = new ArrayList<>();
        for (Trainer trainer : allTrainers) {
            // Access lazy collections *here* while the session is open
            // For example, when checking 'specialization': trainer.getSpecializations().contains(specialization)
            // This will trigger the N+1 query for specializations if not already fetched.

            boolean matchesSpecialization = specialization == null || (trainer.getSpecializations() != null && trainer.getSpecializations().contains(specialization));
            boolean matchesAvailability = available == null || (trainer.getAvailable() != null && trainer.getAvailable().equals(available));
            boolean matchesHourlyRate = maxHourlyRate == null || trainer.getHourlyRate() <= maxHourlyRate;

            if (matchesSpecialization && matchesAvailability && matchesHourlyRate) {
                results.add(new TrainerSearchDTO(trainer)); // Construct DTO, which initializes collections
            }
        }
        return results;

        /*
        // Option 2: Implement more efficient database queries using Spring Data JPA
        // (Recommended for larger datasets and better performance)
        // You would need to add custom methods to your TrainerRepository, e.g.:
        return trainerRepository.findByFilters(specialization, available, maxHourlyRate)
                                .stream()
                                .map(TrainerSearchDTO::new) // Uses the DTO constructor
                                .collect(Collectors.toList());
        */
    }

    /**
     * Get fitness classes by trainer ID
     * @param trainerId Trainer ID
     * @return List of fitness classes for the trainer
     */
    public List<FitnessClass> getFitnessClasses(Long trainerId) {
        Trainer trainer = getTrainerById(trainerId);
        // Assuming getFitnessClasses() returns a List<FitnessClass> or Set<FitnessClass>
        return new ArrayList<>(trainer.getFitnessClasses());
    }
}