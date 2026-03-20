package com.split.expenseSplitter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Your previous Bean Validation handler goes here...
    // @ExceptionHandler(MethodArgumentNotValidException.class) { ... }

    // 2. Handler for custom ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        logger.error("Validation Error: {}", e.getMessage());
        return ResponseEntity.unprocessableEntity()
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        logger.error("Validation Error: {}", e.getMessage());
        return ResponseEntity.unprocessableEntity()
                .body(Map.of("error", e.getMessage()));
    }

    // 3. Handler for custom DuplicateInsertionException
    @ExceptionHandler(DuplicateInsertionException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateInsertionException(DuplicateInsertionException e) {
        logger.error("Duplicate Insertion Error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }
}
