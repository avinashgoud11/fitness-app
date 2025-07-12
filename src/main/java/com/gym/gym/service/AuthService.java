package com.gym.gym.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.UnauthorizedAccessException;
import com.gym.gym.model.JwtUtils;
import com.gym.gym.model.Role;
import com.gym.gym.model.User;
import com.gym.gym.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    

    /**
     * Authenticate user and generate JWT token
     * @param username Username
     * @param password Password
     * @return Authentication response with token
     */
    public Map<String, Object> login(String username, String password) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails);

            // Get user details
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("message", "Login successful");

            return response;
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            throw new UnauthorizedAccessException("Invalid username or password");
        }
    }

    /**
     * Register a new user
     * @param user User to register
     * @return Created user
     */
    public User register(User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_MEMBER);
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Logout user (invalidate token on client side)
     * @param token JWT token to invalidate
     * @return Logout response
     */
    public Map<String, Object> logout(String token) {
        // In a real application, you might want to add the token to a blacklist
        // For now, we'll just return a success message
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("token", token);
        
        return response;
    }

    /**
     * Refresh JWT token
     * @param token Current JWT token
     * @return New JWT token
     */
    public Map<String, Object> refreshToken(String token) {
        try {
            // Extract username from token
            String username = jwtUtils.extractUsername(token);
            
            // Load user details
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            
            // Validate current token
            if (!jwtUtils.validateToken(token, userDetails)) {
                throw new UnauthorizedAccessException("Invalid token");
            }
            
            // Generate new token
            String newToken = jwtUtils.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("message", "Token refreshed successfully");
            
            return response;
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Token refresh failed");
        }
    }

    /**
     * Get current authenticated user
     * @return Current user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Validate JWT token
     * @param token JWT token to validate
     * @return True if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            return jwtUtils.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Change user password
     * @param userId User ID
     * @param currentPassword Current password
     * @param newPassword New password
     * @return Updated user
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedAccessException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Reset user password (admin function)
     * @param userId User ID
     * @param newPassword New password
     * @return Updated user
     */
    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Enable/disable user account
     * @param userId User ID
     * @param enabled Enable status
     * @return Updated user
     */
    public User setUserEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //     return userRepository.findByUsername(username)
    //             .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    // }
} 