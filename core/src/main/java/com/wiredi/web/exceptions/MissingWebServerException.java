package com.wiredi.web.exceptions;

public class MissingWebServerException extends RuntimeException {

    public MissingWebServerException(String message) {
        super(message);
    }
}
