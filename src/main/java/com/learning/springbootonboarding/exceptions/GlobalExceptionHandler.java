package com.learning.springbootonboarding.exceptions;

import com.learning.springbootonboarding.day12.dto.ApiErrorResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // This marks this class as our global error middleware interceptor
public class GlobalExceptionHandler {

    // 1. Catch validation errors triggered by @Valid (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        // Loop through all validation errors to extract the field name and its specific error message
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        String requestId = MDC.get("requestId");

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                "Validation Failed",
                LocalDateTime.now().toString(),
                requestId,
                fieldErrors
        );

        return new ResponseEntity<>(errorPayload, HttpStatus.BAD_REQUEST);
    }

    // 2. Catch generic fallback system errors (RuntimeException, Database crashes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericErrors(Exception ex) {
        String requestId = MDC.get("requestId");
        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                ex.getMessage(),
                LocalDateTime.now().toString(),
                requestId,
                null // No specific field validation errors here
        );

        return new ResponseEntity<>(errorPayload, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
