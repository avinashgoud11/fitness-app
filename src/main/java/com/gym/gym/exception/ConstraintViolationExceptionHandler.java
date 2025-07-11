package com.gym.gym.exception;
import jakarta.validation.ConstraintViolationException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gym.gym.exception.GlobalExceptionHandler.ValidationErrorResponse;

@RestControllerAdvice 
public class ConstraintViolationExceptionHandler {

@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
        ConstraintViolationException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(violation -> {
        String fieldName = violation.getPropertyPath().toString();
        String errorMessage = violation.getMessage();
        errors.put(fieldName, errorMessage);
    });

    ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            request.getDescription(false),
            LocalDateTime.now(),
            errors
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
}
}