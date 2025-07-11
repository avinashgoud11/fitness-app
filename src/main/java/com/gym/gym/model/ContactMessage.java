package com.gym.gym.model;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 200)
    private String subject;

    @NotBlank
    @Size(max = 2000)
    private String message;

    // @NotNull
    private LocalDateTime submissionDate;

   // @NotNull
    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.NEW;

    private String response;
    private LocalDateTime responseDate;
    
    @PrePersist
    public void prePersist() {
    if (submissionDate == null) {
        submissionDate = LocalDateTime.now();
    }
    if (status == null) {
        status = MessageStatus.NEW;
    }
}


    // Getters and Setters
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }
} 