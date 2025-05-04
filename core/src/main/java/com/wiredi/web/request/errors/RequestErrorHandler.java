package com.wiredi.web.request.errors;

import com.wiredi.annotations.Wire;
import com.wiredi.logging.Logging;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.domain.HttpStatus;
import com.wiredi.web.response.ResponseEntity;

import java.util.List;

@Wire
public class RequestErrorHandler {

    private static final Logging logger = Logging.getInstance(RequestErrorHandler.class);
    private final List<RequestExceptionHandler> exceptionHandlerList;
    private final DefaultExceptionHandler defaultExceptionHandler;

    public RequestErrorHandler(List<RequestExceptionHandler> exceptionHandlerList, DefaultExceptionHandler defaultExceptionHandler) {
        this.exceptionHandlerList = exceptionHandlerList;
        this.defaultExceptionHandler = defaultExceptionHandler;
    }

    public void addExceptionHandler(RequestExceptionHandler exceptionHandler) {
        exceptionHandlerList.add(exceptionHandler);
    }

    public ResponseEntity handle(HttpRequest httpRequest, HttpResponse httpResponse, Throwable throwable) {
        try {
            for (RequestExceptionHandler requestExceptionHandler : exceptionHandlerList) {
                if (requestExceptionHandler.handles(throwable)) {
                    ResponseEntity responseEntity = requestExceptionHandler.handle(httpRequest, httpResponse, throwable);
                    if (responseEntity != null) {
                        return responseEntity;
                    }
                }
            }

            return defaultExceptionHandler.handle(httpRequest, httpResponse, throwable);
        } catch (Exception e) {
            logger.error("All ErrorHandlers processing a request threw an exception! This is a serious incident and should not happen!", e);
            return ResponseEntity.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
