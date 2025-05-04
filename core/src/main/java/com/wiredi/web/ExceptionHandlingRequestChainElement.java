package com.wiredi.web;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.lang.Ordered;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.request.HttpRequestChainContext;
import com.wiredi.web.request.HttpRequestChainElement;
import com.wiredi.web.request.errors.RequestErrorHandler;
import com.wiredi.web.response.ResponseEntity;
import com.wiredi.web.response.ResponseRenderingEngine;

@Wire
public class ExceptionHandlingRequestChainElement implements HttpRequestChainElement {

    private final RequestErrorHandler requestErrorHandler;
    private final ResponseRenderingEngine responseRenderingEngine;

    public ExceptionHandlingRequestChainElement(RequestErrorHandler requestErrorHandler, ResponseRenderingEngine responseRenderingEngine) {
        this.requestErrorHandler = requestErrorHandler;
        this.responseRenderingEngine = responseRenderingEngine;
    }

    @Override
    public Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request, HttpRequestChainContext chain) {
        try {
            return chain.next(request);
        } catch (Throwable throwable) {
            ResponseEntity errorResponseEntity = requestErrorHandler.handle(request.details().request(), request.details().request().newResponse(), throwable);
            return responseRenderingEngine.render(errorResponseEntity, request, request.details().request().newResponse());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.FIRST;
    }
}
