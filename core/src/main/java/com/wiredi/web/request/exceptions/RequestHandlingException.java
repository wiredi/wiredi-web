package com.wiredi.web.request.exceptions;

import java.util.ArrayList;
import java.util.List;

public class RequestHandlingException extends RuntimeException {

    private final List<Throwable> nestedExceptions = new ArrayList<>();

    public RequestHandlingException() {
    }

    public RequestHandlingException(String message) {
        super(message);
    }

    public RequestHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestHandlingException(Throwable cause) {
        super(cause);
    }

    public RequestHandlingException addNestedExceptions(List<Throwable> nestedExceptions) {
        this.nestedExceptions.addAll(nestedExceptions);
        return this;
    }

    public RequestHandlingException addNestedException(Throwable nestedException) {
        this.nestedExceptions.add(nestedException);
        return this;
    }
}
