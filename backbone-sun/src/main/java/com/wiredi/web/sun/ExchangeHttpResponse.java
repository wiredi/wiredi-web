package com.wiredi.web.sun;

import com.sun.net.httpserver.HttpExchange;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.domain.HttpVersion;

public class ExchangeHttpResponse extends HttpResponse {
    public ExchangeHttpResponse(HttpVersion httpVersion, HttpExchange exchange) {
        super(httpVersion, exchange.getResponseBody());
    }
}
