package com.wiredi.web.client;

import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageDetails;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.RestClientRequestDetails;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

public class RestClientRequest {

    private final URI uri;
    @NotNull
    private final MessagingEngine messagingEngine;
    @NotNull
    private final HttpMethod httpMethod;
    private final MessageHeaders.Builder headers = new MessageHeaders.Builder();
    private final HttpExchange httpExchange;
    private HttpRequestBody body;
    @NotNull
    private final List<@NotNull RestClientInterceptor> interceptors;

    public RestClientRequest(
            @NotNull HttpMethod httpMethod,
            @NotNull URI uri,
            @NotNull MessagingEngine messagingEngine,
            @NotNull HttpExchange httpExchange,
            @NotNull List<@NotNull RestClientInterceptor> interceptors
    ) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.messagingEngine = messagingEngine;
        this.httpExchange = httpExchange;
        this.interceptors = interceptors;
    }

    public RestClientRequest withBody(Object body) {
        if (body instanceof String s) {
            this.body = new HttpRequestBody.Raw(s.getBytes());
        } else if (body instanceof byte[] b) {
            this.body = new HttpRequestBody.Raw(b);
        } else {
            Message<MessageDetails> serialized = messagingEngine.serialize(body);
            if (serialized.isChunked()) {
                this.body = new HttpRequestBody.Stream(serialized.inputStream());
            } else {
                this.body = new HttpRequestBody.Raw(serialized.body());
            }
        }

        return this;
    }

    public RestClientRequest withBody(String body) {
        this.body = new HttpRequestBody.Raw(body.getBytes());
        return this;
    }

    public RestClientRequest withBody(byte[] body) {
        this.body = new HttpRequestBody.Raw(body);
        return this;
    }

    public RestClientRequest addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public RestClientRequest addHeaders(Consumer<MessageHeaders.Builder> consumer) {
        consumer.accept(headers);
        return this;
    }

    public RestClientResponse exchange() {
        Message<RestClientRequestDetails> requestMessage;

        if (body == null) {
            requestMessage = Message.newEmptyMessage()
                    .withDetails(new RestClientRequestDetails())
                    .addHeaders(headers.build())
                    .build();
        } else {
            if (body instanceof HttpRequestBody.Raw(byte[] bytes)) {
                requestMessage = Message.builder(bytes)
                        .withDetails(new RestClientRequestDetails())
                        .addHeaders(headers.build())
                        .build();
            } else if (body instanceof HttpRequestBody.Stream(java.io.InputStream inputStream)) {
                requestMessage = Message.builder(inputStream)
                        .withDetails(new RestClientRequestDetails())
                        .addHeaders(headers.build())
                        .build();
            } else {
                throw new IllegalArgumentException("Unsupported body type " + body.getClass().getName());
            }
        }
        HttpExchangeRequest request = new HttpExchangeRequest(httpMethod, uri, requestMessage);

        for (RestClientInterceptor interceptor : interceptors) {
            request = interceptor.preExchange(request);
        }

        HttpExchangeResponse response = httpExchange.exchange(request);

        for (RestClientInterceptor interceptor : interceptors) {
            response = interceptor.postExchange(response);
        }

        return new RestClientResponse(response, messagingEngine);
    }
}
