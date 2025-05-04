package com.wiredi.web.domain;

import com.wiredi.runtime.messaging.MessageHeaders;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.Optional;

public abstract class HttpResponse {

    private final MessageHeaders.Builder headers = new MessageHeaders.Builder();
    private final HttpVersion httpVersion;
    private HttpStatusCode responseCode = HttpStatus.OK;
    private final OutputStream responseBody;
    private Long responseLength = null;

    public HttpResponse(HttpVersion httpVersion, OutputStream responseBody) {
        this.httpVersion = httpVersion;
        this.responseBody = responseBody;
    }

    @NotNull
    public MessageHeaders.Builder headers() {
        return headers;
    }

    @NotNull
    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public OutputStream responseBody() {
        return this.responseBody;
    }

    public HttpStatusCode statusCode() {
        return responseCode;
    }

    public void setStatusCode(HttpStatusCode code) {
        this.responseCode = code;
    }

    public Optional<Long> getResponseLength() {
        return Optional.ofNullable(responseLength);
    }

    public void setResponseLength(long responseLength) {
        this.responseLength = responseLength;
    }
}
