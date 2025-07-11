package com.gym.gym.service;

import com.gym.gym.model.ContactMessage;
import com.gym.gym.model.MessageStatus;
import com.gym.gym.repository.ContactMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    /**
     * Create a new contact message
     * @param message Contact message to create
     * @return Created contact message
     */
    public ContactMessage createMessage(ContactMessage message) {
        message.setSubmissionDate(LocalDateTime.now());
        message.setStatus(MessageStatus.NEW);
        return contactMessageRepository.save(message);
    }

    /**
     * Get all contact messages
     * @return List of all contact messages
     */
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }

    /**
     * Get message by ID
     * @param id Message ID
     * @return Message if found
     */
    public ContactMessage getMessageById(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    /**
     * Get messages by status
     * @param status Message status
     * @return List of messages with specified status
     */
    public List<ContactMessage> getMessagesByStatus(MessageStatus status) {
        return contactMessageRepository.findByStatus(status);
    }

    /**
     * Get messages by email
     * @param email Sender's email
     * @return List of messages from specified email
     */
    public List<ContactMessage> getMessagesByEmail(String email) {
        return contactMessageRepository.findByEmail(email);
    }

    /**
     * Update message status
     * @param id Message ID
     * @param status New status
     * @return Updated message
     */
    public ContactMessage updateStatus(Long id, MessageStatus status) {
        ContactMessage message = getMessageById(id);
        message.setStatus(status);
        return contactMessageRepository.save(message);
    }

    /**
     * Add response to message
     * @param id Message ID
     * @param response Response text
     * @return Updated message
     */
    public ContactMessage addResponse(Long id, String response) {
        ContactMessage message = getMessageById(id);
        message.setResponse(response);
        message.setResponseDate(LocalDateTime.now());
        message.setStatus(MessageStatus.RESPONDED);
        return contactMessageRepository.save(message);
    }

    /**
     * Delete message
     * @param id Message ID
     */
    public void deleteMessage(Long id) {
        ContactMessage message = getMessageById(id);
        contactMessageRepository.delete(message);
    }

    /**
     * Get unread message count
     * @return Count of unread messages
     */
    public long getUnreadCount() {
        return contactMessageRepository.countByStatus(MessageStatus.NEW);
    }

    /**
     * Mark message as read
     * @param id Message ID
     * @return Updated message
     */
    public ContactMessage markAsRead(Long id) {
        ContactMessage message = getMessageById(id);
        message.setStatus(MessageStatus.READ);
        return contactMessageRepository.save(message);
    }

    /**
     * Get recent messages
     * @param limit Number of messages to return
     * @return List of recent messages
     */
    public List<ContactMessage> getRecentMessages(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return contactMessageRepository.findTopByOrderBySubmissionDateDesc(pageable);
    }

    /**
     * Search messages by keyword
     * @param keyword Search keyword
     * @return List of matching messages
     */
    public List<ContactMessage> searchMessages(String keyword) {
        return contactMessageRepository.searchByKeyword(keyword);
    }
} 
