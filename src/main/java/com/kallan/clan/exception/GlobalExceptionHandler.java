package com.kallan.clan.exception;

import com.kallan.clan.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseWrapper<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseWrapper.error("The requested resource was not found. Please check your request and try again.", 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
            .body(ResponseWrapper.error(errors.values().iterator().next() + " Please correct the errors and try again.", 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error("An unexpected error occurred. Please try again later.", 500));
    }

    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ResponseWrapper<String>> handleAIServiceException(AIServiceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error("An error occurred while processing your request. Please try again later.", 500));
    }
}