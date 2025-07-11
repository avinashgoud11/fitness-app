package com.gym.gym.controller;

import com.gym.gym.model.User;
import com.gym.gym.service.AuthService;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.UnauthorizedAccessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login endpoint
     * @param loginRequest Login credentials
     * @return Authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }

            Map<String, Object> response = authService.login(username, password);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed"));
        }
    }

    /**
     * User registration endpoint
     * @param user User to register
     * @return Created user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = authService.register(user);
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", createdUser
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed"));
        }
    }

    /**
     * User logout endpoint
     * @param logoutRequest Logout request with token
     * @return Logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> logoutRequest) {
        try {
            String token = logoutRequest.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
            }

            Map<String, Object> response = authService.logout(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Logout failed"));
        }
    }

    /**
     * Refresh JWT token endpoint
     * @param refreshRequest Refresh request with current token
     * @return New JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        try {
            String token = refreshRequest.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
            }

            Map<String, Object> response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Token refresh failed"));
        }
    }

    /**
     * Get current authenticated user
     * @return Current user details
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User currentUser = authService.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get user details"));
        }
    }

    /**
     * Validate JWT token
     * @param tokenRequest Token to validate
     * @return Validation result
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> tokenRequest) {
        try {
            String token = tokenRequest.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
            }

            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Token validation failed"));
        }
    }

    /**
     * Change user password
     * @param passwordRequest Password change request
     * @return Updated user
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordRequest) {
        try {
            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Current and new passwords are required"));
            }

            User currentUser = authService.getCurrentUser();
            User updatedUser = authService.changePassword(currentUser.getId(), currentPassword, newPassword);
            
            return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully",
                "user", updatedUser
            ));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Password change failed"));
        }
    }

    /**
     * Reset user password (admin only)
     * @param userId User ID
     * @param resetRequest Reset request with new password
     * @return Updated user
     */
    @PostMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId, @RequestBody Map<String, String> resetRequest) {
        try {
            String newPassword = resetRequest.get("newPassword");
            if (newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
            }

            User updatedUser = authService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully",
                "user", updatedUser
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Password reset failed"));
        }
    }

    /**
     * Enable/disable user account (admin only)
     * @param userId User ID
     * @param enableRequest Enable/disable request
     * @return Updated user
     */
    @PostMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setUserEnabled(@PathVariable Long userId, @RequestBody Map<String, Boolean> enableRequest) {
        try {
            Boolean enabled = enableRequest.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Enabled status is required"));
            }

            User updatedUser = authService.setUserEnabled(userId, enabled);
            return ResponseEntity.ok(Map.of(
                "message", "User " + (enabled ? "enabled" : "disabled") + " successfully",
                "user", updatedUser
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update user status"));
        }
    }
} 