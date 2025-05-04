package com.wiredi.web.messaging;

import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpRequest;

public class HttpRequestMessageDetails implements HttpMessageDetails {

    private final HttpRequest request;
    private final HttpEndpoint httpEndpoint;

    public HttpRequestMessageDetails(HttpRequest request) {
        this.request = request;
        httpEndpoint = HttpEndpoint.of(request);
    }

    public HttpRequest request() {
        return request;
    }

    public HttpEndpoint httpEndpoint() {
        return httpEndpoint;
    }
}
