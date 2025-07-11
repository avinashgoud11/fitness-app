package com.gym.gym.controller;

import com.gym.gym.model.User;
import com.gym.gym.model.Role;
import com.gym.gym.service.UserService;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.dto.UpdateUserDTO;
import com.gym.gym.exception.DuplicateResourceException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Create a new user
     * @param user User details
     * @return Created user
     */
    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().body("Duplicate user: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
        }
    }

    /**
     * Get all users
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * @param id User ID
     * @return User if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update user
     * @param id User ID
     * @param updatedUser Updated user details
     * @return Updated user
     */
    // @PutMapping("/{id}")
    // public ResponseEntity<?> updateUser(
    //         @PathVariable Long id,
    //         @Valid @RequestBody User updatedUser,
    //         BindingResult bindingResult) {
    //     if (bindingResult.hasErrors()) {
    //         return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
    //     }
    //     try {
    //         User user = userService.updateUser(id, updatedUser);
    //         return ResponseEntity.ok(user);
    //     } catch (DuplicateResourceException e) {
    //         return ResponseEntity.badRequest().body("Duplicate user: " + e.getMessage());
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.badRequest().body("Error updating user: " + e.getMessage());
    //     }
    // }

    /**
     * Delete user
     * @param id User ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Cannot delete user: existing references in the system");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
    }
}


    /**
     * Get user by username
     * @param username Username
     * @return User if found
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by email
     * @param email Email
     * @return User if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email exists
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Update user password
     * @param id User ID
     * @param newPassword New password
     * @return Updated user
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        try {
            User user = userService.updatePassword(id, newPassword);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating password: " + e.getMessage());
        }
    }

    /**
     * Update user role
     * @param id User ID
     * @param role New role
     * @return Updated user
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        try {
            User user = userService.updateRole(id, role);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating role: " + e.getMessage());
        }
    }

    /**
     * Enable/disable user
     * @param id User ID
     * @param enabled Enabled status
     * @return Updated user
     */
    @PutMapping("/{id}/enabled")
    public ResponseEntity<?> updateEnabledStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        try {
            User user = userService.updateEnabledStatus(id, enabled);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating enabled status: " + e.getMessage());
        }
    }
@PutMapping("/{id}")
public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
    userService.updateUser(id, dto); // Ensure UserService has updateUser(Long, UpdateUserDTO)
    return ResponseEntity.ok("User updated");
}


    /**
     * Get users by role
     * @param role Role to filter by
     * @return List of users with role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        Role parsedRole = Role.valueOf(role.toUpperCase());
        List<User> users = userService.getUsersByRole(parsedRole);
        return ResponseEntity.ok(users);
    }

    /**
     * Get enabled users
     * @return List of enabled users
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<User>> getEnabledUsers() {
        List<User> users = userService.getEnabledUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get disabled users
     * @return List of disabled users
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<User>> getDisabledUsers() {
        List<User> users = userService.getDisabledUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Search users by criteria
     * @param username Optional username filter
     * @param email Optional email filter
     * @param role Optional role filter
     * @param enabled Optional enabled status filter
     * @return List of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean enabled) {
        List<User> users = userService.searchUsers(username, email, role, enabled);
        return ResponseEntity.ok(users);
    }
} 