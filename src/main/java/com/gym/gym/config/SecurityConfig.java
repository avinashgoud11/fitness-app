package com.gym.gym.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.gym.gym.model.JwtAuthenticationFilter;
import com.gym.gym.model.JwtUtils;
import com.gym.gym.service.CustomUserDetailsService; // Import your CustomUserDetailsService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService; // Inject CustomUserDetailsService
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection for dependencies
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService customUserDetailsService, // Added CustomUserDetailsService
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService; // Assign it
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    // This bean now correctly returns your CustomUserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    mvcMatcherBuilder.pattern("/"),
                    mvcMatcherBuilder.pattern("/favicon.ico"),
                    mvcMatcherBuilder.pattern("/hello"),
                    mvcMatcherBuilder.pattern("/api/hello"),
                    mvcMatcherBuilder.pattern("/greeting"),
                    mvcMatcherBuilder.pattern("/api/greeting"),
                    mvcMatcherBuilder.pattern("/status"),
                    mvcMatcherBuilder.pattern("/api/status"),
                    mvcMatcherBuilder.pattern("/error"),
                    mvcMatcherBuilder.pattern("/api/error")
                ).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/auth/**"), mvcMatcherBuilder.pattern("/api/auth/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/admins/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/class-bookings")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/class-bookings/{bookingId}/cancel")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/class-bookings/{bookingId}/status")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/class-bookings/{bookingId}")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/class-bookings/member/{memberId}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/class-bookings/class/{classId}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/class-bookings/{bookingId}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/class-bookings/active")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/class-bookings/cancelled")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/progress")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/progress/{id}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/progress/{id}")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/progress")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/progress/{id}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/progress/member/{memberId}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/progress/member/{memberId}/date-range")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/progress/member/{memberId}/recent")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/members/**")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/members/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/classes/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/classes/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/classes/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/classes/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/workouts/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/workouts/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/workouts/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/workouts/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/contact-messages")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/contact-messages/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/users")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/{id}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/username/**")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/email/**")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/role/**")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/enabled")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/disabled")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/search")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}/password")).authenticated()
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}/role")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}/enabled")).hasAuthority("ROLE_ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/users/{id}")).hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
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
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500", "https://legendary-ganache-a2dcf3.netlify.app/", "https://cheery-maamoul-a63002.netlify.app/", "https://fitness-app-0zk0.onrender.com"));
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