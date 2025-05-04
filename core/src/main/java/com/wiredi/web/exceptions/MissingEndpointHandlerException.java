package com.wiredi.web.exceptions;

public class MissingEndpointHandlerException extends RuntimeException {
    public MissingEndpointHandlerException(String message) {
        super(message);
    }
}
