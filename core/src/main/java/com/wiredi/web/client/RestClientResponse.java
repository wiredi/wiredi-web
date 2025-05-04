package com.wiredi.web.client;

import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.domain.HttpStatusCode;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public class RestClientResponse {

    @NotNull
    private final HttpExchangeResponse httpResponse;
    @NotNull
    private final MessagingEngine messagingEngine;

    public RestClientResponse(
            @NotNull HttpExchangeResponse httpResponse,
            @NotNull MessagingEngine messagingEngine
    ) {
        this.httpResponse = httpResponse;
        this.messagingEngine = messagingEngine;
    }

    public <T> T body(Class<T> bodyType) {
        return messagingEngine.deserialize(httpResponse.message(), bodyType);
    }

    public String bodyAsString() {
        return new String(httpResponse.message().body());
    }

    public byte[] body() {
        return httpResponse.message().body();
    }

    public InputStream bodyAsStream() {
        return httpResponse.message().inputStream();
    }

    public HttpStatusCode statusCode() {
        return httpResponse.statusCode();
    }

    public boolean isOk() {
        return statusCode().is2xxSuccessful();
    }

    public boolean isError() {
        return statusCode().isError();
    }
}
