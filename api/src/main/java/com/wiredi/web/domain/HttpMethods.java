package com.wiredi.web.domain;


public enum HttpMethods {

    GET(HttpMethod.GET),
    HEAD(HttpMethod.HEAD),
    POST(HttpMethod.POST),
    PUT(HttpMethod.PUT),
    PATCH(HttpMethod.PATCH),
    DELETE(HttpMethod.DELETE),
    OPTIONS(HttpMethod.OPTIONS),
    TRACE(HttpMethod.TRACE);

    private final HttpMethod httpMethod;

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    HttpMethods(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
