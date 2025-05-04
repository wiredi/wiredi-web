package com.wiredi.web.integration;

import com.wiredi.annotations.Wire;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.lang.Ordered;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessagingResult;
import com.wiredi.runtime.messaging.errors.UnsupportedMessagingResultException;
import com.wiredi.web.ReadableWebServerContext;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.domain.HttpStatus;
import com.wiredi.web.exceptions.NotFoundException;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.request.HttpRequestChainContext;
import com.wiredi.web.request.HttpRequestChainElement;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Wire(proxy = false)
public class RequestDispatcherHandler implements HttpRequestChainElement {
    private static final Logging logger = Logging.getInstance(RequestDispatcherHandler.class);

    private final ReadableWebServerContext webServerContext;
    private final HandlerMethodRegistry handlerRegistry;

    public RequestDispatcherHandler(
            ReadableWebServerContext webServerContext,
            HandlerMethodRegistry handlerRegistry
    ) {
        this.webServerContext = webServerContext;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    @NotNull
    public Message<HttpResponseMessageDetails> handle(
            @NotNull Message<HttpRequestMessageDetails> message,
            HttpRequestChainContext chain
    ) {
        HttpRequest httpRequest = message.details().request();
        HttpResponse httpResponse = httpRequest.newResponse();

        logger.debug(() -> "Received request %s: %s".formatted(
                httpRequest.requestStatusLine().httpMethod(),
                httpRequest.requestStatusLine().uri())
        );

        MessagingResult messagingResult = webServerContext.getMessagingEngine()
                .processMessage(message, finalMessage -> {
                    List<HandlerMethod> handlers = handlerRegistry.resolveHandler(httpRequest);
                    for (HandlerMethod handler : handlers) {
                        ResponseEntity entity = handler.invoke(message);
                        if (entity != null) {
                            return entity;
                        }
                    }

                    throw new NotFoundException("All available handlers did not return a response entity");
                });

        if (messagingResult.wasSuccessful()) {
            ResponseEntity responseEntity = messagingResult.getResultAs();
            if (responseEntity != null) {
                Message<HttpResponseMessageDetails> responseMessage = webServerContext.getResponseRenderingEngine()
                        .render(responseEntity, message, httpResponse);

                if (responseMessage != null) {
                    return responseMessage;
                }
            }
        }

        if (messagingResult.wasSkipped()) {
            return webServerContext.getResponseRenderingEngine()
                    .render(ResponseEntity.notFound(), message, httpResponse);
        }

        Throwable error = messagingResult.errorOr(() -> new UnsupportedMessagingResultException(messagingResult));
        ResponseEntity errorResponseEntity = webServerContext.getRequestErrorHandler()
                .handle(httpRequest, httpResponse, error);

        return webServerContext.getResponseRenderingEngine()
                .render(errorResponseEntity, message, httpResponse);
    }

    @Override
    public int getOrder() {
        return Ordered.LAST;
    }
}