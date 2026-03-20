package com.split.expenseSplitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 1. This annotation tells Spring to watch ALL controllers for errors
@RestControllerAdvice
public class GlobalValidationExceptionHandler {

    // 2. This tells Spring: "If a validation error happens, send it to this method!"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // 3. Loop through all the fields that failed validation
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage(); // This is your custom message!

            errors.put(fieldName, errorMessage);
        });

        // 4. Return a clean 400 Bad Request with just the field names and messages
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
