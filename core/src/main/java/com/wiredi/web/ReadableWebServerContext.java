package com.wiredi.web;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.request.HttpRequestChainElement;
import com.wiredi.web.request.errors.RequestErrorHandler;
import com.wiredi.web.request.errors.RequestExceptionHandler;
import com.wiredi.web.response.ResponseRenderingEngine;

import java.util.ArrayList;
import java.util.List;

@Wire(proxy = false)
public class ReadableWebServerContext implements WebServerContext {

    private final List<HttpRequestChainElement> interceptors = new ArrayList<>();
    private final RequestErrorHandler requestErrorHandler;
    private MessagingEngine messagingEngine;
    private ResponseRenderingEngine responseRenderingEngine;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ReadableWebServerContext(
            RequestErrorHandler requestErrorHandler,
            MessagingEngine messagingEngine,
            ResponseRenderingEngine responseRenderingEngine
    ) {
        this.requestErrorHandler = requestErrorHandler;
        this.messagingEngine = messagingEngine;
        this.responseRenderingEngine = responseRenderingEngine;
    }

    @Override
    public void setPathMatcher(AntPathMatcher antPathMatcher) {
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    public void addRequestInterceptor(HttpRequestChainElement interceptor) {
        this.interceptors.add(interceptor);
    }

    @Override
    public void addExceptionHandler(RequestExceptionHandler exceptionHandler) {
        requestErrorHandler.addExceptionHandler(exceptionHandler);
    }

    @Override
    public void setMessagingEngine(MessagingEngine messagingEngine) {
        this.messagingEngine = messagingEngine;
    }

    @Override
    public void setResponseRenderingEngine(ResponseRenderingEngine responseRenderingEngine) {
        this.responseRenderingEngine = responseRenderingEngine;
    }

    public ResponseRenderingEngine getResponseRenderingEngine() {
        return responseRenderingEngine;
    }

    public MessagingEngine getMessagingEngine() {
        return messagingEngine;
    }

    public RequestErrorHandler getRequestErrorHandler() {
        return requestErrorHandler;
    }

    public List<HttpRequestChainElement> getHttpRequestInterceptors() {
        return this.interceptors;
    }

    public AntPathMatcher getAntPathMatcher() {
        return antPathMatcher;
    }
}
