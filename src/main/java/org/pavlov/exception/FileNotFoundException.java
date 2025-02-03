package org.pavlov.exception;

import lombok.Getter;

@Getter
public class FileNotFoundException extends RuntimeException{

    public String keyMessage;

    public FileNotFoundException(String message, String keyMessage) {
        super(message);
        this.keyMessage = keyMessage;
    }
}
