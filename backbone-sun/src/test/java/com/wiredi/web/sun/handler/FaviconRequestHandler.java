package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.resources.builtin.ClassPathResource;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

@Wire(proxy = false)
public class FaviconRequestHandler implements HandlerMethod {

    private final ClassPathResource faviconResource = new ClassPathResource("favicon.ico");

    @Override
    public @NotNull ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message) {
        if (faviconResource.doesNotExist()) {
            return ResponseEntity.notFound();
        }

        return ResponseEntity.ok(faviconResource);
    }

    @Override
    public @NotNull HttpEndpoint endpoint() {
        return new HttpEndpoint("/favicon.ico", HttpMethod.GET);
    }
}
