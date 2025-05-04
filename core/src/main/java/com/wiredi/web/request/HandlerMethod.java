package com.wiredi.web.request;

import com.wiredi.runtime.lang.Ordered;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.integration.HandlerMethodRegistry;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A handler that will handle endpoint invocations.
 * <p>
 * A handler specifies the {@link #endpoint()} it is going to handle.
 * Any request to the pattern with the http method will be delegated to the implementation that specifies it.
 * <p>
 * Consequently, only one handler for the combination of pattern + http method may exist.
 * <p>
 * Any implementation is expected to construct a {@link ResponseEntity} that is used for rendering the response body.
 * <p>
 * The {@link com.wiredi.web.integration.HandlerMethodRegistry} allows you to register multiple handlers for the same pattern + http method.
 * This registry is used to resolve the correct handler for a given request.
 * If your handler returns null when invoked, the next handler for the same {@link HttpEndpoint} is invoked.
 */
public interface HandlerMethod extends Ordered {

    /**
     * Handle the invocations of the pattern + http method combination.
     * <p>
     * This method is responsible for invoking business code and translate the business code result to the {@link HttpResponse}.
     * <p>
     * It should construct a view that compiles the response body.
     * Never should the {@link HttpResponse} be modified in this step.
     * <p>
     * If this handler returns null, the request is handled by the next handler, if any is available for the same {@link #endpoint()}.
     *
     * @param message the received message.
     * @return a View
     */
    @Nullable
    ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message);

    /**
     * The pattern that this handler wants to be invoked for.
     *
     * @return a pattern supporting ant pattern patterns
     */
    @NotNull
    HttpEndpoint endpoint();

    /**
     * This function is invoked when the handler is registered at the HandlerMethodRegistry.
     *
     * @param registry the HandlerMethodRegistry in which this handler is registered
     */
    default void registered(HandlerMethodRegistry registry) {
        // NoOp
    }
}
