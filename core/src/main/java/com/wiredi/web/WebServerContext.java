package com.wiredi.web;

import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.request.HttpRequestChainElement;
import com.wiredi.web.request.errors.RequestExceptionHandler;
import com.wiredi.web.response.ResponseRenderingEngine;

public interface WebServerContext {

    void setMessagingEngine(MessagingEngine messagingEngine);

    void setPathMatcher(AntPathMatcher antPathMatcher);

    void addRequestInterceptor(HttpRequestChainElement interceptor);

    void addExceptionHandler(RequestExceptionHandler exceptionHandler);

    void setResponseRenderingEngine(ResponseRenderingEngine responseRenderingEngine);
}
