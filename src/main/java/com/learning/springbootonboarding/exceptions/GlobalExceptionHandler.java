package com.learning.springbootonboarding.exceptions;

import com.learning.springbootonboarding.weekThreeFourFive.dto.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validation errors (@Valid) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // Use getFieldErrors() instead of getAllErrors() to avoid ClassCastException on class-level constraints
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.merge(
                        error.getField(),
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + "; " + replacement
                )
        );

        log.warn("Validation failed on {} field(s): {}", fieldErrors.size(), fieldErrors.keySet());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", fieldErrors);
    }

    // 2. Database Unique Constraint / Integrity Errors -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String message = "Database constraint violation";

        if (ex.getMessage() != null && ex.getMessage().contains("uk_project_name")) {
            message = "Duplicate project name";
            fieldErrors.put("name", "Project name already exists");
        }

        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildResponse(HttpStatus.CONFLICT, message, fieldErrors.isEmpty() ? null : fieldErrors);
    }

    // 3. EntityNotFoundException -> 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // 4. IllegalArgumentException -> 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // 5. ResponseStatusException -> Preserves exact HTTP status
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        log.warn("ResponseStatusException [{}]: {}", ex.getStatusCode().value(), message);
        return buildResponse(ex.getStatusCode(), message, null);
    }

    // 6. Keycloak / External RestClient errors -> Logs full error internally, masks details from client
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClientException(RestClientResponseException ex) {
        log.error("External auth service error [{}]: {}", ex.getStatusCode().value(), ex.getResponseBodyAsString(), ex);
        return buildResponse(ex.getStatusCode(), "Authentication service error", null);
    }

    // 7. Spring Security AccessDeniedException -> 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", null);
    }

    // 8. Fallback for all unhandled exceptions -> 500 (NEVER expose raw ex.getMessage() to clients)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericErrors(Exception ex) {
        log.error("Unhandled internal server error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred", null);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatusCode status,
            String message,
            Map<String, String> fieldErrors) {

        ApiErrorResponse errorPayload = new ApiErrorResponse(
                status.value(),
                message,
                LocalDateTime.now().toString(),
                MDC.get("requestId"),
                fieldErrors
        );
        return new ResponseEntity<>(errorPayload, status);
    }
}
