package com.wiredi.web.exceptions;

import com.wiredi.web.domain.HttpStatusCode;
import org.jetbrains.annotations.Nullable;

public class HttpStatusCodeException extends RuntimeException {

    private final HttpStatusCode statusCode;
    @Nullable
    private Object response;

    public HttpStatusCodeException(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatusCodeException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCodeException(HttpStatusCode statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCodeException(HttpStatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCode statusCode() {
        return statusCode;
    }

    public HttpStatusCodeException withResponse(Object response) {
        this.response = response;
        return this;
    }

    @Nullable
    public Object response() {
        return response;
    }
}
