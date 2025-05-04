package com.wiredi.web.request.errors;

import com.wiredi.annotations.Order;
import com.wiredi.annotations.Wire;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.exceptions.HttpStatusCodeException;
import com.wiredi.web.response.ResponseEntity;

@Wire(proxy = false)
@Order(Order.AUTO_CONFIGURATION)
public class DefaultStatusCodeExceptionHandler implements RequestExceptionHandler {

    @Override
    public boolean handles(Throwable throwable) {
        return throwable instanceof HttpStatusCodeException;
    }

    @Override
    public ResponseEntity handle(HttpRequest httpRequest, HttpResponse httpResponse, Throwable throwable) {
        HttpStatusCodeException statusCodeException = (HttpStatusCodeException) throwable;
        ResponseEntity responseEntity = ResponseEntity.of(statusCodeException.statusCode());
        if (statusCodeException.response() != null) {
            responseEntity.withBody(statusCodeException.response());
        }

        return responseEntity;
    }
}
