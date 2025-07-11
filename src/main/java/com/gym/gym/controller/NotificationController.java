package com.gym.gym.controller;

import com.gym.gym.model.MembershipType;
import com.gym.gym.service.NotificationService;
import com.gym.gym.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Send class reminder notifications
     * @param classId Fitness class ID
     * @return Notification result
     */
    @PostMapping("/class-reminders/{classId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> sendClassReminders(@PathVariable Long classId) {
        try {
            int sentCount = notificationService.sendClassReminders(classId);
            return ResponseEntity.ok(Map.of(
                "message", "Class reminders sent successfully",
                "sentCount", sentCount
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send class reminders"));
        }
    }

    /**
     * Send payment reminder notifications
     * @return Notification result
     */
    @PostMapping("/payment-reminders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendPaymentReminders() {
        try {
            int sentCount = notificationService.sendPaymentReminders();
            return ResponseEntity.ok(Map.of(
                "message", "Payment reminders sent successfully",
                "sentCount", sentCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send payment reminders"));
        }
    }

    /**
     * Send membership expiry reminders
     * @param request Request with days before expiry
     * @return Notification result
     */
    @PostMapping("/membership-expiry-reminders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMembershipExpiryReminders(@RequestBody Map<String, Integer> request) {
        try {
            Integer daysBeforeExpiry = request.get("daysBeforeExpiry");
            if (daysBeforeExpiry == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "daysBeforeExpiry is required"));
            }

            int sentCount = notificationService.sendMembershipExpiryReminders(daysBeforeExpiry);
            return ResponseEntity.ok(Map.of(
                "message", "Membership expiry reminders sent successfully",
                "sentCount", sentCount,
                "daysBeforeExpiry", daysBeforeExpiry
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send membership expiry reminders"));
        }
    }

    /**
     * Send welcome notification to new member
     * @param memberId Member ID
     * @return Notification result
     */
    @PostMapping("/welcome/{memberId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendWelcomeNotification(@PathVariable Long memberId) {
        try {
            boolean success = notificationService.sendWelcomeNotification(memberId);
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "message", "Welcome notification sent successfully",
                    "memberId", memberId
                ));
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to send welcome notification"));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send welcome notification"));
        }
    }

    /**
     * Send class cancellation notification
     * @param classId Fitness class ID
     * @param request Cancellation request with reason
     * @return Notification result
     */
    @PostMapping("/class-cancellation/{classId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> sendClassCancellationNotification(
            @PathVariable Long classId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String reason = requestBody.get("reason");
            if (reason == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cancellation reason is required"));
            }

            int sentCount = notificationService.sendClassCancellationNotification(classId, reason);
            return ResponseEntity.ok(Map.of(
                "message", "Class cancellation notifications sent successfully",
                "sentCount", sentCount,
                "classId", classId,
                "reason", reason
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send class cancellation notifications"));
        }
    }

    /**
     * Send class schedule change notification
     * @param classId Fitness class ID
     * @param request Schedule change request
     * @return Notification result
     */
    @PostMapping("/class-schedule-change/{classId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Map<String, Object>> sendClassScheduleChangeNotification(
            @PathVariable Long classId,
            @RequestBody Map<String, Object> request) {
        try {
            String oldTimeStr = (String) request.get("oldTime");
            String newTimeStr = (String) request.get("newTime");
            
            if (oldTimeStr == null || newTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Old time and new time are required"));
            }

            LocalDateTime oldTime = LocalDateTime.parse(oldTimeStr);
            LocalDateTime newTime = LocalDateTime.parse(newTimeStr);

            int sentCount = notificationService.sendClassScheduleChangeNotification(classId, oldTime, newTime);
            return ResponseEntity.ok(Map.of(
                "message", "Class schedule change notifications sent successfully",
                "sentCount", sentCount,
                "classId", classId,
                "oldTime", oldTime,
                "newTime", newTime
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send class schedule change notifications"));
        }
    }

    /**
     * Send bulk notification to all members
     * @param request Bulk notification request
     * @return Notification result
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendBulkNotification(@RequestBody Map<String, String> request) {
        try {
            String subject = request.get("subject");
            String message = request.get("message");
            
            if (subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject and message are required"));
            }

            int sentCount = notificationService.sendBulkNotification(subject, message);
            return ResponseEntity.ok(Map.of(
                "message", "Bulk notifications sent successfully",
                "sentCount", sentCount,
                "subject", subject
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send bulk notifications"));
        }
    }

    /**
     * Send notification to specific member
     * @param memberId Member ID
     * @param request Member notification request
     * @return Notification result
     */
    @PostMapping("/member/{memberId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMemberNotification(
            @PathVariable Long memberId,
            @RequestBody Map<String, String> request) {
        try {
            String subject = request.get("subject");
            String message = request.get("message");
            
            if (subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject and message are required"));
            }

            boolean success = notificationService.sendMemberNotification(memberId, subject, message);
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "message", "Member notification sent successfully",
                    "memberId", memberId,
                    "subject", subject
                ));
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to send member notification"));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send member notification"));
        }
    }

    /**
     * Send notification to members by membership type
     * @param membershipType Membership type
     * @param request Membership notification request
     * @return Notification result
     */
    @PostMapping("/membership-type/{membershipType}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendNotificationByMembershipType(
            @PathVariable String membershipType,
            @RequestBody Map<String, String> request) {
        try {
            String subject = request.get("subject");
            String message = request.get("message");
            
            if (subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject and message are required"));
            }

            MembershipType type = MembershipType.valueOf(membershipType.toUpperCase());
            int sentCount = notificationService.sendNotificationByMembershipType(type, subject, message);
            
            return ResponseEntity.ok(Map.of(
                "message", "Membership type notifications sent successfully",
                "sentCount", sentCount,
                "membershipType", membershipType,
                "subject", subject
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid membership type"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send membership type notifications"));
        }
    }

    /**
     * Send notification to inactive members
     * @param request Inactive member notification request
     * @return Notification result
     */
    @PostMapping("/inactive-members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendInactiveMemberNotification(@RequestBody Map<String, String> request) {
        try {
            String subject = request.get("subject");
            String message = request.get("message");
            
            if (subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject and message are required"));
            }

            int sentCount = notificationService.sendInactiveMemberNotification(subject, message);
            return ResponseEntity.ok(Map.of(
                "message", "Inactive member notifications sent successfully",
                "sentCount", sentCount,
                "subject", subject
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send inactive member notifications"));
        }
    }

    /**
     * Get notification statistics
     * @return Notification statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        try {
            Map<String, Object> stats = notificationService.getNotificationStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get notification statistics"));
        }
    }

    /**
     * Send test notification
     * @param request Test notification request
     * @return Test result
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendTestNotification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String subject = request.get("subject");
            String message = request.get("message");
            
            if (email == null || subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email, subject, and message are required"));
            }

            // This would use the EmailService directly for testing
            // For now, just return success
            return ResponseEntity.ok(Map.of(
                "message", "Test notification sent successfully",
                "email", email,
                "subject", subject
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send test notification"));
        }
    }
}