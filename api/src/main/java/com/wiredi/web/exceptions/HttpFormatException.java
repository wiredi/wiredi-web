package com.wiredi.web.exceptions;

public class HttpFormatException extends RuntimeException {

    public HttpFormatException(String message) {
        super(message);
    }

    public HttpFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpFormatException(Throwable cause) {
        super(cause);
    }
}
