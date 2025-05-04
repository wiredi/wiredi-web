package com.wiredi.web.integration;

import com.wiredi.annotations.Wire;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.lang.Ordered;
import com.wiredi.runtime.lang.OrderedComparator;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.exceptions.NotFoundException;
import com.wiredi.web.request.HandlerMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Registry for managing and resolving HandlerMethods.
 * Provides functionality to register handlers and resolve the appropriate handler for incoming requests.
 */
@Wire(proxy = false)
public class HandlerMethodRegistry {

    private static final Logging logger = Logging.getInstance(HandlerMethodRegistry.class);
    private final EndpointRegistry<List<HandlerMethod>> handlerRegistry = new EndpointRegistry<>();

    public HandlerMethodRegistry(@NotNull List<HandlerMethod> handlers) {
        handlers.forEach(this::register);
    }

    /**
     * Registers a new handler method.
     * Throws IllegalStateException if a handler for the same pattern pattern and HTTP method already exists.
     *
     * @param handler the handler method to register
     * @throws IllegalStateException if a duplicate handler is found
     */
    public void register(@NotNull HandlerMethod handler) {
        HttpEndpoint endpoint = handler.endpoint();
        List<HandlerMethod> handlerMethods = handlerRegistry.computeIfAbsent(endpoint, (e) -> new ArrayList<>());
        handlerMethods.add(handler);
        OrderedComparator.sort(handlerMethods);
        handler.registered(this);
        logger.debug(() -> "Registered handler for %s".formatted(endpoint));
    }

    /**
     * Resolves the appropriate handler method for a given HTTP request.
     *
     * @param request the HTTP request to find a handler for
     * @return the matching handler method
     * @throws NotFoundException if no matching handler is found
     */
    @NotNull
    public List<HandlerMethod> resolveHandler(@NotNull HttpRequest request) {
        HttpEndpoint endpoint = HttpEndpoint.of(request);

        return Optional.ofNullable(handlerRegistry.get(endpoint))
                .orElseThrow(() -> new NotFoundException(
                        "Could not determine a handler to process %s".formatted(endpoint))
                );
    }

    /**
     * Returns the total number of registered handlers.
     *
     * @return number of registered handlers
     */
    public int size() {
        return handlerRegistry.size();
    }
}