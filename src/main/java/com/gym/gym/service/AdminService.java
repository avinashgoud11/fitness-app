package com.gym.gym.service;

import com.gym.gym.model.Admin;
import com.gym.gym.model.User;
import com.gym.gym.repository.AdminRepository;
import com.gym.gym.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // Make sure this import is correct

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false) // Inject PasswordEncoder if available, set to false if not always present
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new admin
     */
    @Transactional // Ensures the entire operation is atomic
    public Admin createAdmin(Admin admin) {
        User user = admin.getUser();

        // 1. Handle the associated User entity
        if (user != null) {
            if (user.getId() != null) {
                // Scenario 1: User object has an ID. Assume it's an existing user.
                // Fetch the user from the database to ensure it's a managed entity
                // within the current Hibernate session.
                User existingUser = userRepository.findById(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));
                admin.setUser(existingUser);
            } else if (user.getUsername() != null) {
                // Scenario 2: User object has no ID, but a username is provided.
                // Try to find an existing user by username.
                Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());
                if (existingUserOpt.isPresent()) {
                    // User found, link the managed existing user entity to the admin.
                    admin.setUser(existingUserOpt.get());
                } else {
                    // Scenario 3: No existing user found by username. This means it's a brand new user.
                    // The incoming 'user' object is transient/detached. It needs to be persisted.
                    // Ensure the ID is null so JPA knows to generate it for a new entity.
                    user.setId(null);

                    // IMPORTANT: Encode the password for the new user before saving,
                    // if your User entity's password field will store encoded passwords.
                    if (user.getPassword() != null && passwordEncoder != null) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    } else if (user.getPassword() == null) {
                        throw new IllegalArgumentException("New user must have a password.");
                    }
                    // Save the new User entity first. This makes it a managed entity.
                    User savedUser = userRepository.save(user);
                    // Now, set the managed (newly saved) User entity on the Admin object.
                    admin.setUser(savedUser);
                }
            } else {
                // If a User object is provided but it lacks an ID or username, it's ambiguous.
                throw new IllegalArgumentException("User details within Admin must include either an ID or a username (and password for new users).");
            }
        } else {
            // An Admin must be associated with a User in this application's context.
            throw new IllegalArgumentException("Admin creation requires an associated user.");
        }

        // 2. Validate admin code uniqueness or generate it
        if (admin.getAdminCode() == null || admin.getAdminCode().isEmpty()) {
            admin.setAdminCode(generateAdminCode());
        } else if (adminRepository.existsByAdminCode(admin.getAdminCode())) {
            throw new DuplicateResourceException("Admin code already exists: " + admin.getAdminCode());
        }

        // 3. Validate that the user is not already linked to an existing admin
        // This check uses the ID of the *managed* user that has now been set on the admin object.
        if (admin.getUser() != null && admin.getUser().getId() != null) {
            if (adminRepository.existsByUserId(admin.getUser().getId())) {
                throw new DuplicateResourceException("User is already an admin");
            }
        }

        // 4. Set creation and update timestamps, and active status
        // The Admin constructor already sets createdAt and updatedAt, but good to be explicit here
        // or remove from constructor if you prefer service to manage this.
        if (admin.getCreatedAt() == null) {
            admin.setCreatedAt(LocalDateTime.now());
        }
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setActive(true); // Ensure admin is active on creation

        // 5. Save the Admin. The associated User is now correctly handled (either managed existing or newly persisted).
        return adminRepository.save(admin);
    }

    // --- Rest of your AdminService methods remain the same and are good ---

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
    }

    public Admin getAdminByCode(String adminCode) {
        return adminRepository.findByAdminCode(adminCode)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with code: " + adminCode));
    }

    public Admin getAdminByUserId(Long userId) {
        return adminRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found for user id: " + userId));
    }

    public Admin getAdminByEmail(String email) {
        // Assuming UserRepository has findByEmail or User has unique email
        return adminRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + email));
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public List<Admin> getActiveAdmins() {
        return adminRepository.findByActiveTrue();
    }

    public List<Admin> getAdminsByDepartment(String department) {
        return adminRepository.findByDepartment(department);
    }

    public List<Admin> getAdminsByAccessLevel(String accessLevel) {
        return adminRepository.findByAccessLevel(accessLevel);
    }

    public Admin updateAdmin(Long id, Admin adminDetails) {
        Admin admin = getAdminById(id);

        if (!admin.getAdminCode().equals(adminDetails.getAdminCode()) &&
                adminRepository.existsByAdminCode(adminDetails.getAdminCode())) {
            throw new DuplicateResourceException("Admin code already exists: " + adminDetails.getAdminCode());
        }

        admin.setAdminCode(adminDetails.getAdminCode());
        admin.setDepartment(adminDetails.getDepartment());
        admin.setAccessLevel(adminDetails.getAccessLevel());
        admin.setActive(adminDetails.isActive());
        admin.setUpdatedAt(LocalDateTime.now());

        return adminRepository.save(admin);
    }

    public Admin updateAdminAccessLevel(Long id, String accessLevel) {
        Admin admin = getAdminById(id);
        admin.setAccessLevel(accessLevel);
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Admin updateAdminDepartment(Long id, String department) {
        Admin admin = getAdminById(id);
        admin.setDepartment(department);
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Admin activateAdmin(Long id) {
        Admin admin = getAdminById(id);
        admin.setActive(true);
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Admin deactivateAdmin(Long id) {
        Admin admin = getAdminById(id);
        admin.setActive(false);
        admin.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        Admin admin = getAdminById(id);
        adminRepository.delete(admin);
    }

    public boolean isUserAdmin(Long userId) {
        return adminRepository.existsByUserId(userId);
    }

    public boolean adminCodeExists(String adminCode) {
        return adminRepository.existsByAdminCode(adminCode);
    }

    public AdminStatistics getAdminStatistics() {
        List<Admin> allAdmins = adminRepository.findAll();
        List<Admin> activeAdmins = adminRepository.findByActiveTrue();

        AdminStatistics stats = new AdminStatistics();
        stats.setTotalAdmins(allAdmins.size());
        stats.setActiveAdmins(activeAdmins.size());
        stats.setInactiveAdmins(allAdmins.size() - activeAdmins.size());

        long fullAccess = allAdmins.stream().filter(a -> "FULL".equals(a.getAccessLevel())).count();
        long limitedAccess = allAdmins.stream().filter(a -> "LIMITED".equals(a.getAccessLevel())).count();
        long readOnly = allAdmins.stream().filter(a -> "READ_ONLY".equals(a.getAccessLevel())).count();

        stats.setFullAccessAdmins((int) fullAccess);
        stats.setLimitedAccessAdmins((int) limitedAccess);
        stats.setReadOnlyAdmins((int) readOnly);

        return stats;
    }

    public String generateAdminCode() {
        String prefix = "ADM";
        int counter = 1;
        String adminCode = prefix + String.format("%04d", counter);

        while (adminRepository.existsByAdminCode(adminCode)) {
            counter++;
            adminCode = prefix + String.format("%04d", counter);
        }

        return adminCode;
    }

    public Admin createAdminFromUser(Long userId, String department, String accessLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (adminRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("User is already an admin");
        }

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setAdminCode(generateAdminCode());
        admin.setDepartment(department);
        admin.setAccessLevel(accessLevel);
        admin.setActive(true);

        return adminRepository.save(admin);
    }

    public static class AdminStatistics {
        private int totalAdmins;
        private int activeAdmins;
        private int inactiveAdmins;
        private int fullAccessAdmins;
        private int limitedAccessAdmins;
        private int readOnlyAdmins;

        public int getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(int totalAdmins) { this.totalAdmins = totalAdmins; }
        public int getActiveAdmins() { return activeAdmins; }
        public void setActiveAdmins(int activeAdmins) { this.activeAdmins = activeAdmins; }
        public int getInactiveAdmins() { return inactiveAdmins; }
        public void setInactiveAdmins(int inactiveAdmins) { this.inactiveAdmins = inactiveAdmins; }
        public int getFullAccessAdmins() { return fullAccessAdmins; }
        public void setFullAccessAdmins(int fullAccessAdmins) { this.fullAccessAdmins = fullAccessAdmins; }
        public int getLimitedAccessAdmins() { return limitedAccessAdmins; }
        public void setLimitedAccessAdmins(int limitedAccessAdmins) { this.limitedAccessAdmins = limitedAccessAdmins; }
        public int getReadOnlyAdmins() { return readOnlyAdmins; }
        public void setReadOnlyAdmins(int readOnlyAdmins) { this.readOnlyAdmins = readOnlyAdmins; }
    }
}