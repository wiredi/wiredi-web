package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

@Wire(proxy = false)
public class ErrorProvokingHandler implements HandlerMethod {
    @Override
    public @NotNull ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message) {
        throw new IllegalArgumentException("Test");
    }

    @Override
    public @NotNull HttpEndpoint endpoint() {
        return new HttpEndpoint("/error", HttpMethod.GET);
    }
}
