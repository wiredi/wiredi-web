package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.annotations.aspects.Pure;
import com.wiredi.runtime.Environment;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.resources.Resource;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.PathVariables;
import com.wiredi.web.domain.HttpHeaders;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;

@Wire(proxy = false)
public class ResourceHandler implements HandlerMethod {

    private final Environment environment;

    public ResourceHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    public @NotNull ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message) {
        PathVariables pathVariables = new PathVariables(endpoint().pattern(), message.details().request().path());
        String resourcePath = pathVariables.get("resource");
        Resource resource = environment.loadResource("classpath:" + resourcePath);

        if (resource.doesNotExist()) {
            return ResponseEntity.notFound();
        }

        int size;
        try {
            size =resource.getInputStream().available();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return ResponseEntity.ok(resource)
                .withHeaders(headers -> headers.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(size)));
    }

    @Override
    public @NotNull HttpEndpoint endpoint() {
        return new HttpEndpoint("/resources/{resource}", HttpMethod.GET);
    }
}
