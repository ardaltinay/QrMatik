package com.feasymenu.server.exception;

import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Environment env;

    public GlobalExceptionHandler(Environment env) {
        this.env = env;
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ApiError> handleAccountSuspended(AccountSuspendedException ex, WebRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), "account_suspended", req, null);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "not_found", req, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, WebRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "bad_request", req, null);
    }

    @ExceptionHandler(PlanFeatureUnavailableException.class)
    public ResponseEntity<ApiError> handlePlanFeature(PlanFeatureUnavailableException ex, WebRequest req) {
        return build(HttpStatus.PAYMENT_REQUIRED, ex.getMessage(), "feature_unavailable", req, null);
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ResponseEntity<ApiError> handlePlanLimit(PlanLimitExceededException ex, WebRequest req) {
        // Preserve legacy semantic where controllers returned 402 for plan limits
        return build(HttpStatus.PAYMENT_REQUIRED, ex.getMessage(), "plan_limit", req, null);
    }

    @ExceptionHandler(TableUnavailableException.class)
    public ResponseEntity<ApiError> handleTableUnavailable(TableUnavailableException ex, WebRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), "table_unavailable", req, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest req) {
        // Return a generic conflict message instead of raw DB error details in response
        String message = "Database constraint violation. The record might already exist or is linked to other data.";
        return build(HttpStatus.CONFLICT, message, "data_integrity_error", req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", "validation_error", req, details);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, WebRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return build(status, ex.getReason(), status.toString(), req, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "illegal_argument", req, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, WebRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), "illegal_state", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest req) {
        boolean prod = isProd();
        String msg = prod ? "An unexpected error occurred." : ex.getMessage();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, msg, "internal_error", req, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String code, WebRequest req,
            List<String> details) {
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message,
                req.getDescription(false), code, details == null ? Collections.emptyList() : details);
        return ResponseEntity.status(status).body(body);
    }

    private boolean isProd() {
        try {
            for (String p : env.getActiveProfiles()) {
                if (p != null && p.toLowerCase().contains("prod"))
                    return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
