package com.ayoub.orders.error;

import com.ayoub.orders.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }

        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "VALIDATION_ERROR",
                "Validation failed",
                details
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "BAD_REQUEST",
                "Invalid parameter value",
                Map.of(
                        "name", ex.getName(),
                        "value", String.valueOf(ex.getValue()),
                        "expectedType", ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName()
                )
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "BAD_REQUEST",
                "Malformed JSON or invalid enum value",
                Map.of()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(OrderNotFoundException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "NOT_FOUND",
                ex.getMessage(),
                Map.of("orderId", ex.getOrderId().toString())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidTransition(InvalidOrderStatusTransitionException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "CONFLICT",
                ex.getMessage(),
                Map.of(
                        "orderId", ex.getOrderId().toString(),
                        "from", ex.getFrom().name(),
                        "to", ex.getTo().name()
                )
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ApiError> handleIdempotencyConflict(IdempotencyConflictException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "CONFLICT",
                ex.getMessage(),
                Map.of("idempotencyKey", ex.getKey())
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "BAD_REQUEST",
                ex.getMessage(),
                ex.getDetails()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimistic(OptimisticLockingFailureException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "CONFLICT",
                "Concurrent update detected. Please retry.",
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                req.getRequestURI(),
                "INTERNAL_ERROR",
                "Unexpected error",
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
