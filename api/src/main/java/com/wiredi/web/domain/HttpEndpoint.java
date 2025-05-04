package com.wiredi.web.domain;

import com.wiredi.web.messaging.HttpRequestMessageDetails;

public record HttpEndpoint(String pattern, HttpMethod method) {

    public static HttpEndpoint of(HttpRequest request) {
        return new HttpEndpoint(request.path(), request.requestStatusLine().httpMethod());
    }

    public static HttpEndpoint of(HttpRequestMessageDetails details) {
        return of(details.request());
    }

}
