package com.gym.gym.controller;
import com.gym.gym.model.ContactMessage;
import com.gym.gym.model.MessageStatus;
import com.gym.gym.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/contact-messages")
@CrossOrigin(origins = "*")
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    /**
     * Create a new contact message
     * @param message Contact message details
     * @return Created message
     */
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody ContactMessage message) {
        try {
            ContactMessage createdMessage = contactMessageService.createMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all contact messages
     * @return List of all messages
     */
    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Get message by ID
     * @param id Message ID
     * @return Message if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContactMessage> getMessageById(@PathVariable Long id) {
        try {
            ContactMessage message = contactMessageService.getMessageById(id);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get messages by status
     * @param status Message status
     * @return List of messages with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContactMessage>> getMessagesByStatus(@PathVariable MessageStatus status) {
        List<ContactMessage> messages = contactMessageService.getMessagesByStatus(status);
        return ResponseEntity.ok(messages);
    }

    /**
     * Add response to message
     * @param id Message ID
     * @param response Response text
     * @return Updated message
     */
    @PutMapping("/{id}/respond")
    public ResponseEntity<?>addResponse(
            @PathVariable Long id,
            @RequestBody String response) {
        try {
            ContactMessage message = contactMessageService.addResponse(id, response);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete message
     * @param id Message ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        try {
            contactMessageService.deleteMessage(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get count of unread messages
     * @return Number of unread messages
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        long count = contactMessageService.getUnreadCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get recent messages
     * @param limit Maximum number of messages to return
     * @return List of recent messages
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ContactMessage>> getRecentMessages(@RequestParam(defaultValue = "10") int limit) {
        List<ContactMessage> messages = contactMessageService.getRecentMessages(limit);
        return ResponseEntity.ok(messages);
    }

    /**
     * Search messages by keyword
     * @param keyword Search keyword
     * @return List of matching messages
     */
    @GetMapping("/search")
    public ResponseEntity<List<ContactMessage>> searchMessages(@RequestParam String keyword) {
        List<ContactMessage> messages = contactMessageService.searchMessages(keyword);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by email
     * @param email Email address
     * @return List of messages from email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<List<ContactMessage>> getMessagesByEmail(@PathVariable String email) {
        List<ContactMessage> messages = contactMessageService.getMessagesByEmail(email);
        return ResponseEntity.ok(messages);
    }

    /**
     * Update message status
     * @param id Message ID
     * @param status New status
     * @return Updated message
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam MessageStatus status) {
        try {
            ContactMessage message = contactMessageService.updateStatus(id, status);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}