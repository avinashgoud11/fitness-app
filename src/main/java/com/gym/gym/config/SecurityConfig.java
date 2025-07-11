package com.gym.gym.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gym.gym.model.JwtAuthenticationFilter;
import com.gym.gym.model.JwtUtils;
import com.gym.gym.service.AuthService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    // @Bean
    // public UserDetailsService userDetailsService(AuthService authService) {
    //     return authService;
    // }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

   @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Allow pre-flight OPTIONS requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // Publicly accessible general endpoints
            .requestMatchers(
                "/hello", "/api/hello",
                "/greeting", "/api/greeting",
                "/status", "/api/status",
                "/error", "/api/error"
            ).permitAll()

            // Authentication endpoints are public
            .requestMatchers("/auth/**", "/api/auth/**").permitAll()

            // Admin-only endpoints
            .requestMatchers("/api/admins/**").hasAuthority("ROLE_ADMIN")

            // Class Bookings Endpoints (Granular Control) - THIS IS MUCH BETTER
            .requestMatchers(HttpMethod.POST, "/api/class-bookings").authenticated() // Members can create bookings
            .requestMatchers(HttpMethod.PUT, "/api/class-bookings/{bookingId}/cancel").authenticated() // Members can cancel their own bookings (service will validate ownership)
            .requestMatchers(HttpMethod.PUT, "/api/class-bookings/{bookingId}/status").hasAuthority("ROLE_ADMIN") // Only admins can update booking status
            .requestMatchers(HttpMethod.DELETE, "/api/class-bookings/{bookingId}").hasAuthority("ROLE_ADMIN") // Only admins can delete bookings

            // GET requests for class bookings
            .requestMatchers(HttpMethod.GET, "/api/class-bookings/member/{memberId}").authenticated() // Members can view their own bookings, admins can view any
            .requestMatchers(HttpMethod.GET, "/api/class-bookings/class/{classId}").authenticated() // Members can view bookings for a specific class (e.g., to see who's attending)
            .requestMatchers(HttpMethod.GET, "/api/class-bookings/{bookingId}").authenticated() // Members can view their specific booking, admins can view any
            .requestMatchers(HttpMethod.GET, "/api/class-bookings/active").hasAuthority("ROLE_ADMIN") // Only admins can see all active bookings
            .requestMatchers(HttpMethod.GET, "/api/class-bookings/cancelled").hasAuthority("ROLE_ADMIN") // Only admins can see all cancelled bookings
// Fitness Progress Endpoints (Granular Control)
.requestMatchers(HttpMethod.POST, "/api/progress").authenticated() // Members can create their own progress
.requestMatchers(HttpMethod.PUT, "/api/progress/{id}").authenticated() // Members can update their own progress (service will validate ownership)
.requestMatchers(HttpMethod.DELETE, "/api/progress/{id}").hasAuthority("ROLE_ADMIN") // Only admins can delete progress

// GET requests for fitness progress
.requestMatchers(HttpMethod.GET, "/api/progress").hasAuthority("ROLE_ADMIN") // Only admins can view all progress entries
.requestMatchers(HttpMethod.GET, "/api/progress/{id}").authenticated() // Members can view their specific progress, admins can view any
.requestMatchers(HttpMethod.GET, "/api/progress/member/{memberId}").authenticated() // Members can view their own progress, admins can view any
.requestMatchers(HttpMethod.GET, "/api/progress/member/{memberId}/date-range").authenticated() // Members can view their own progress in range, admins can view any
.requestMatchers(HttpMethod.GET, "/api/progress/member/{memberId}/recent").authenticated() // Members can view their own recent progress, admins can view any

            // Other API endpoints - apply granular control as recommended previously - THIS IS ALSO MUCH BETTER
            .requestMatchers(HttpMethod.GET, "/api/members/**").authenticated() // Members can view their own profile, Admins can view all
            .requestMatchers("/api/members/**").hasAuthority("ROLE_ADMIN") // Admins can manage all members
            .requestMatchers(HttpMethod.POST, "/api/classes/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/classes/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/classes/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/classes/**").permitAll() // Anyone can view classes schedules

            .requestMatchers(HttpMethod.POST, "/api/workouts/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/workouts/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/workouts/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/workouts/**").permitAll() // Example: view workout plans

            .requestMatchers(HttpMethod.POST, "/api/contact-messages").permitAll() // Anyone can send a message
            .requestMatchers("/api/contact-messages/**").hasAuthority("ROLE_ADMIN") // Only admins can manage contact messages (GET, PUT, DELETE)

            // User Management Endpoints (Granular Control)
.requestMatchers(HttpMethod.POST, "/api/users").permitAll() // Allow new user registration
.requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN") // Only admins get all users
.requestMatchers(HttpMethod.GET, "/api/users/{id}").authenticated() // Users can fetch their own (add backend check)
.requestMatchers(HttpMethod.GET, "/api/users/username/**").authenticated()
.requestMatchers(HttpMethod.GET, "/api/users/email/**").authenticated()
.requestMatchers(HttpMethod.GET, "/api/users/role/**").hasAuthority("ROLE_ADMIN")
.requestMatchers(HttpMethod.GET, "/api/users/enabled").hasAuthority("ROLE_ADMIN")
.requestMatchers(HttpMethod.GET, "/api/users/disabled").hasAuthority("ROLE_ADMIN")
.requestMatchers(HttpMethod.GET, "/api/users/search").hasAuthority("ROLE_ADMIN")

.requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated() // Users update themselves (add backend check)
.requestMatchers(HttpMethod.PUT, "/api/users/{id}/password").authenticated()
.requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasAuthority("ROLE_ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/users/{id}/enabled").hasAuthority("ROLE_ADMIN")

.requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("ROLE_ADMIN")

            // All remaining requests must be authenticated
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
