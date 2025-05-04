package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpHeaders;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.domain.MediaType;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

@Wire(proxy = false)
public class GeneralRequestHandler implements HandlerMethod {
    @Override
    public @NotNull ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message) {
        String path = message.details().request().path();

        return ResponseEntity.ok(new Response(path)).withHeaders(headers -> headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    @Override
    public @NotNull HttpEndpoint endpoint() {
        return new HttpEndpoint("/**", HttpMethod.GET);
    }

    public record Response(
            String path
    ) {}
}
