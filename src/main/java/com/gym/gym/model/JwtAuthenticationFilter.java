package com.gym.gym.model;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        logger.debug("DEBUG JWT Filter: Processing request for path: {}", path);

        try {
            String jwt = parseJwt(request);
            logger.debug("DEBUG JWT Filter: Parsed JWT: {}", (jwt != null ? jwt.substring(0, Math.min(jwt.length(), 20)) + "..." : "null"));

            // IMPORTANT: Removed the problematic jwtUtils.validateToken(jwt, null) here.
            // We just check if JWT exists; actual validation happens after loading UserDetails.
            if (jwt != null) {
                String username = jwtUtils.extractUsername(jwt); // Assuming extractUsername can work without UserDetails
                logger.debug("DEBUG JWT Filter: Extracted username: {}", username);

                // Only proceed if username is found and no authentication is currently in context
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // IMPORTANT: Check if userDetails is null after loading
                    if (userDetails == null) {
                        logger.warn("DEBUG JWT Filter: UserDetails not found for username: {}", username);
                        // Do NOT set authentication. Continue the filter chain.
                        // Spring Security's later filters will handle unauthorized access based on authorizeHttpRequests.
                    } else {
                        // Now, validate the token against the loaded userDetails
                        if (jwtUtils.validateToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            logger.debug("DEBUG JWT Filter: Authentication object to set: {}", authentication);
                            logger.debug("DEBUG JWT Filter: Authentication authorities: {}", authentication.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            logger.debug("DEBUG JWT Filter: SecurityContextHolder after set: {}", SecurityContextHolder.getContext().getAuthentication());
                        } else {
                            logger.warn("DEBUG JWT Filter: Invalid JWT token for user: {}", username);
                        }
                    }
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    logger.debug("DEBUG JWT Filter: SecurityContextHolder already has authentication for path: {}", request.getServletPath());
                } else {
                    logger.warn("DEBUG JWT Filter: Username could not be extracted or is null, or SecurityContextHolder not null for path: {}", request.getServletPath());
                }
            } else {
                logger.debug("DEBUG JWT Filter: JWT is null for path: {}", path);
            }
        } catch (Exception e) {
            logger.error("DEBUG JWT Filter: Cannot set user authentication or token validation failed: {}", e.getMessage(), e);
        }

        logger.debug("DEBUG JWT Filter: SecurityContextHolder before filterChain.doFilter: {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);

        logger.debug("DEBUG JWT Filter: SecurityContextHolder after filterChain.doFilter: {}", SecurityContextHolder.getContext().getAuthentication());
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    // Ensure the isPublicEndpoint method is removed as discussed previously,
    // as authorization should be handled solely by SecurityConfig.
}