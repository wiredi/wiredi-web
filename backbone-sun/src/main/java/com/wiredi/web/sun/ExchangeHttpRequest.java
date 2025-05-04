package com.wiredi.web.sun;

import com.sun.net.httpserver.HttpExchange;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.web.Paths;
import com.wiredi.web.domain.*;

import java.util.Arrays;

public class ExchangeHttpRequest extends HttpRequest {

    private final HttpExchange exchange;

    protected ExchangeHttpRequest(
            HttpExchange exchange
    ) {
        super(
                new RequestStatusLine(HttpMethod.valueOf(exchange.getRequestMethod()), exchange.getRequestURI(), HttpVersion.parse(exchange.getProtocol())),
                mapHeaders(exchange.getRequestHeaders()),
                exchange.getRequestBody(),
                Paths.resolvePath(exchange.getRequestURI(), exchange.getHttpContext().getPath())
        );
        this.exchange = exchange;
    }

    private static MessageHeaders mapHeaders(com.sun.net.httpserver.Headers headers) {
        final MessageHeaders.Builder httpHeaders = MessageHeaders.builder();
        headers.forEach((headerName, values) -> httpHeaders.addAll(headerName, values.stream().flatMap(it -> Arrays.stream(it.split(","))).map(it -> MessageHeader.of(headerName, it)).toList()));
        return httpHeaders.build();
    }

    @Override
    public HttpResponse newResponse() {
        return new ExchangeHttpResponse(requestStatusLine().httpVersion(), exchange);
    }
}
