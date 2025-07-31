package com.gym.gym.controller;

// TODO: Replace with the correct import for ContactForm, for example:
import com.gym.gym.model.ContactForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://legendary-ganache-a2dcf3.netlify.app")
public class ContactController {

    // In-memory rate limiter
    private final Map<String, RateLimit> requestCounts = new ConcurrentHashMap<>();

    @PostMapping("/contact")
    public ResponseEntity<?> handleContactForm(@RequestBody @Valid ContactForm contactForm,
@RequestHeader("X-Forwarded-For") String ipAddress) {
        if (!isAllowed(ipAddress)) {
            return ResponseEntity.status(429).body(Map.of("message", "Too many requests. Please try again later."));
        }

        // Simulate save or email send
        return ResponseEntity.ok(Map.of("message", "Message sent successfully"));
    }

    private boolean isAllowed(String ip) {
        final long WINDOW = 15 * 60; // seconds
        final int MAX_REQUESTS = 5;

        RateLimit rateLimit = requestCounts.computeIfAbsent(ip, k -> new RateLimit());
        long now = Instant.now().getEpochSecond();

        if (now - rateLimit.windowStart >= WINDOW) {
            rateLimit.windowStart = now;
            rateLimit.requestCount = 1;
            return true;
        }

        if (rateLimit.requestCount < MAX_REQUESTS) {
            rateLimit.requestCount++;
            return true;
        }

        return false;
    }

    static class RateLimit {
        long windowStart = Instant.now().getEpochSecond();
        int requestCount = 0;
    }
}
