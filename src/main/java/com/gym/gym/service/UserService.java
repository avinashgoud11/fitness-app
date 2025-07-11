package com.gym.gym.service;

import com.gym.gym.model.User;
import com.gym.gym.model.Role;
import com.gym.gym.repository.UserRepository;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.dto.UpdateUserDTO;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     * @param user User details
     * @return Created user
     */
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_MEMBER);
        }

        return userRepository.save(user);
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * @param id User ID
     * @return User if found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Update user
     * @param id User ID
     * @param updatedUser Updated user details
     * @return Updated user
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        // Check if username is being changed and if it already exists
        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Update fields
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setEnabled(updatedUser.isEnabled());

        // Update password only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    /**
     * Delete user
     * @param id User ID
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /**
     * Get user by username
     * @param username Username
     * @return User if found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user by email
     * @param email Email
     * @return User if found
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user password
     * @param id User ID
     * @param newPassword New password
     * @return Updated user
     */
    public User updatePassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Update user role
     * @param id User ID
     * @param role New role
     * @return Updated user
     */
    public User updateRole(Long id, Role role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Enable/disable user
     * @param id User ID
     * @param enabled Enabled status
     * @return Updated user
     */
    public User updateEnabledStatus(Long id, boolean enabled) {
        User user = getUserById(id);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    /**
     * Get users by role
     * @param role Role to filter by
     * @return List of users with role
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .toList();
    }

    /**
     * Get enabled users
     * @return List of enabled users
     */
    public List<User> getEnabledUsers() {
        return userRepository.findAll().stream()
                .filter(User::isEnabled)
                .toList();
    }

    /**
     * Get disabled users
     * @return List of disabled users
     */
    public List<User> getDisabledUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isEnabled())
                .toList();
    }

    /**
     * Search users by criteria
     * @param username Optional username filter
     * @param email Optional email filter
     * @param role Optional role filter
     * @param enabled Optional enabled status filter
     * @return List of matching users
     */
    public List<User> searchUsers(String username, String email, Role role, Boolean enabled) {
        return userRepository.findAll().stream()
                .filter(user -> username == null || user.getUsername().contains(username))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> enabled == null || user.isEnabled() == enabled)
                .toList();
    }
    public void updateUser(Long id, UpdateUserDTO dto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

    if (dto.getUsername() != null) user.setUsername(dto.getUsername());
    if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
    if (dto.getLastName() != null) user.setLastName(dto.getLastName());
    if (dto.getEmail() != null) user.setEmail(dto.getEmail());
    if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
    if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
    if (dto.getRole() != null) user.setRole(dto.getRole());

    userRepository.save(user);
}


}