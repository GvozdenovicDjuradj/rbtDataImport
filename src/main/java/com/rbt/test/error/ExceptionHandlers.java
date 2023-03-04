package com.rbt.test.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlers {
    @ExceptionHandler(RbtException.class)
    ResponseEntity<ApiErrorResponse> handleApiException(RbtException ex) {
        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getCode())).
                body(new ApiErrorResponse(ex.getCode(), LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.valueOf(400))
                .body(new ApiErrorResponse(400, LocalDateTime.now(), ex.getMessage()));
    }


    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.valueOf(403))
                .body(new ApiErrorResponse(403, LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErrorResponse> handleAccessDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.valueOf(400))
                .body(new ApiErrorResponse(400, LocalDateTime.now(), ex.getMessage()));
    }
}
