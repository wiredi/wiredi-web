package com.wiredi.web.response;

import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.web.domain.HttpStatus;
import com.wiredi.web.domain.HttpStatusCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ResponseEntity {

    @Nullable
    private Object body = null;
    @NotNull
    private final MessageHeaders.Builder headers = new MessageHeaders.Builder();
    @NotNull
    private final HttpStatusCode statusCode;

    public ResponseEntity(
            @NotNull HttpStatusCode statusCode
    ) {
        this.statusCode = statusCode;
    }

    public static ResponseEntity of(HttpStatusCode statusCode) {
        return new ResponseEntity(statusCode);
    }

    public static ResponseEntity ok() {
        return of(HttpStatus.OK);
    }

    public static ResponseEntity ok(Object body) {
        return ok().withBody(body);
    }

    public static ResponseEntity notFound() {
        return of(HttpStatus.NOT_FOUND);
    }

    public @Nullable Object getBody() {
        return body;
    }

    public @NotNull MessageHeaders.Builder getHeaders() {
        return headers;
    }

    public @NotNull HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public ResponseEntity withBody(@Nullable Object body) {
        if (body == null) {
            return this;
        }
        if (this.body != null) {
            throw new IllegalStateException("Tried to overwrite body " + this.body + " with " + body);
        }
        this.body = body;
        return this;
    }

    public ResponseEntity withHeaders(Consumer<MessageHeaders.Builder> headersConsumer) {
        headersConsumer.accept(headers);
        return this;
    }
}
