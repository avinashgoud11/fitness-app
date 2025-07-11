package com.gym.gym.service;
import com.gym.gym.model.User;
import com.gym.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service // Mark this as a Spring Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by username from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

       // CRITICAL PART: Ensure the correct role is granted
      // CORRECT

List<GrantedAuthority> authorities = new ArrayList<>();
// Add the role as a GrantedAuthority
authorities.add(new SimpleGrantedAuthority(user.getRole().name())); // user.getRole().name() should be "ROLE_ADMIN"

        // Add a print statement here to verify the authorities:
        System.out.println("User loaded: " + username + ", Assigned Authorities: " + authorities);


        // Build and return Spring Security's UserDetails object
        // The password MUST be the encoded password from your database
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // This is the encoded password from the database
                 authorities // Empty list for authorities/roles for now, if not handled in your User model
                // If your User model has roles, you'd convert them here:
                // user.getRoles().stream()
                //     .map(role -> new SimpleGrantedAuthority(role.name())) // Assuming Role is an enum or string
                //     .collect(Collectors.toList())
        );
    }
}
