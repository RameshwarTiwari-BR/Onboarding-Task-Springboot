package com.learning.springbootonboarding.exceptions;

import com.learning.springbootonboarding.weekThreeFourFive.dto.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catch validation errors triggered by @Valid (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

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

    // 2. Catch Database Unique Constraint / Integrity Errors
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String requestId = MDC.get("requestId");
        Map<String, String> fieldErrors = new HashMap<>();
        String message = "Database constraint violation";

        // Check if the error came from our specific unique constraint
        if (ex.getMessage() != null && ex.getMessage().contains("uk_project_name")) {
            message = "Duplicate project name";
            fieldErrors.put("name", "Project name already exists");
        }

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(), // 409
                message,
                LocalDateTime.now().toString(),
                requestId,
                fieldErrors.isEmpty() ? null : fieldErrors
        );

        return new ResponseEntity<>(errorPayload, HttpStatus.CONFLICT);
    }

    // 3. Catch EntityNotFoundException (missing Project or Task IDs) -> 404 Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        String requestId = MDC.get("requestId");

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                ex.getMessage(),
                LocalDateTime.now().toString(),
                requestId,
                null
        );

        return new ResponseEntity<>(errorPayload, HttpStatus.NOT_FOUND);
    }


    // 4. IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex){
        String requestId = MDC.get("requestId");

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                requestId,
                null
        );
        return new ResponseEntity<>(errorPayload, HttpStatus.BAD_REQUEST);
    }

    // 5. Catch ResponseStatusException (401, 403, 409, etc.) and preserve its exact HTTP status
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        String requestId = MDC.get("requestId");
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                ex.getStatusCode().value(),
                message,
                LocalDateTime.now().toString(),
                requestId,
                null
        );

        return new ResponseEntity<>(errorPayload, ex.getStatusCode());
    }


    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClientException(RestClientResponseException ex) {
        String requestId = MDC.get("requestId");

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                ex.getStatusCode().value(),
                "Authentication service error",
                LocalDateTime.now().toString(),
                requestId,
                null
        );

        return new ResponseEntity<>(errorPayload, ex.getStatusCode());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex){
        String requestId = MDC.get("requestId");

        ApiErrorResponse errorResponse = new ApiErrorResponse(
               HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                LocalDateTime.now().toString(),
                requestId,
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


    // 4. Catch generic fallback system errors (RuntimeException, Database crashes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericErrors(Exception ex) {
        String requestId = MDC.get("requestId");
        ApiErrorResponse errorPayload = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                ex.getMessage(),
                LocalDateTime.now().toString(),
                requestId,
                null
        );

        return new ResponseEntity<>(errorPayload, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
