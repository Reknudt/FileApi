package org.pavlov.controller;

import lombok.AllArgsConstructor;
import org.pavlov.dto.response.ErrorResponse;
import org.pavlov.exception.FileNotFoundException;
import org.pavlov.exception.ResourceNotFoundException;
import org.pavlov.exception.UserAlreadyExistsException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class ControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFileNotFound(FileNotFoundException e) {
        String localizedMessage = messageSource.getMessage(
                e.getMessage(),
                new Object[]{e.getKeyMessage()},
                LocaleContextHolder.getLocale());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(localizedMessage)
                .build();
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(SecurityException e) {
        String localizedMessage = messageSource.getMessage(
                e.getMessage(),
                new Object[]{e.getMessage()},
                LocaleContextHolder.getLocale());

        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(localizedMessage)
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}