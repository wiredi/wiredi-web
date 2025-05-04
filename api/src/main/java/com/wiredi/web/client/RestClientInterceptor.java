package com.wiredi.web.client;

/**
 * An interface to allow interceptions of the
 */
public interface RestClientInterceptor {

    default HttpExchangeRequest preExchange(HttpExchangeRequest request) {
        return request;
    }

    default HttpExchangeResponse postExchange(HttpExchangeResponse response) {
        return response;
    }
}
