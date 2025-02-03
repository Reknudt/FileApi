package org.pavlov.controller;


import lombok.AllArgsConstructor;
import org.pavlov.dto.response.ErrorResponse;
import org.pavlov.exception.FileNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}