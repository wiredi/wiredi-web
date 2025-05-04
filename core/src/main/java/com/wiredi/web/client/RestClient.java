package com.wiredi.web.client;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.UriBuilder;
import com.wiredi.web.client.dispatcher.JavaHttpExchange;
import com.wiredi.web.domain.HttpMethod;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Wire(proxy = false)
public class RestClient {

    @NotNull
    private final List<@NotNull RestClientInterceptor> interceptors = new ArrayList<>();
    @NotNull
    private final HttpExchange httpExchange;
    @NotNull
    private MessagingEngine messagingEngine;

    public RestClient(
            @NotNull List<@NotNull RestClientInterceptor> interceptors,
            @NotNull HttpExchange httpExchange,
            @NotNull MessagingEngine messagingEngine
    ) {
        this.httpExchange = httpExchange;
        this.messagingEngine = messagingEngine;
        this.interceptors.addAll(interceptors);
    }

    public static RestClient create(@NotNull MessagingEngine messageConverters) {
        return new RestClient(Collections.emptyList(), new JavaHttpExchange(), messageConverters);
    }

    public RestClient addInterceptor(@NotNull RestClientInterceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public RestClient setMessagingEngine(@NotNull MessagingEngine messagingEngine) {
        this.messagingEngine = messagingEngine;
        return this;
    }

    public @NotNull RestClientRequest get(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.GET, pathBuilder);
    }

    public @NotNull RestClientRequest get(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.GET, pathBuilder);
    }

    public @NotNull RestClientRequest get(@NotNull URI uri) {
        return method(HttpMethod.GET, uri);
    }

    public @NotNull RestClientRequest post(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.POST, pathBuilder);
    }

    public @NotNull RestClientRequest post(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.POST, pathBuilder);
    }

    public @NotNull RestClientRequest post(@NotNull URI uri) {
        return method(HttpMethod.POST, uri);
    }

    public @NotNull RestClientRequest put(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.PUT, pathBuilder);
    }

    public @NotNull RestClientRequest put(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.PUT, pathBuilder);
    }

    public @NotNull RestClientRequest put(@NotNull URI uri) {
        return method(HttpMethod.PUT, uri);
    }

    public @NotNull RestClientRequest delete(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.DELETE, pathBuilder);
    }

    public @NotNull RestClientRequest delete(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.DELETE, pathBuilder);
    }

    public @NotNull RestClientRequest delete(@NotNull URI uri) {
        return method(HttpMethod.DELETE, uri);
    }

    public @NotNull RestClientRequest head(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.HEAD, pathBuilder);
    }

    public @NotNull RestClientRequest head(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.HEAD, pathBuilder);
    }

    public @NotNull RestClientRequest head(@NotNull URI uri) {
        return method(HttpMethod.HEAD, uri);
    }

    public @NotNull RestClientRequest options(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.OPTIONS, pathBuilder);
    }

    public @NotNull RestClientRequest options(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.OPTIONS, pathBuilder);
    }

    public @NotNull RestClientRequest options(@NotNull URI uri) {
        return method(HttpMethod.OPTIONS, uri);
    }

    public @NotNull RestClientRequest trace(@NotNull Consumer<@NotNull UriBuilder> pathBuilderConsumer) {
        UriBuilder pathBuilder = UriBuilder.prepare();
        pathBuilderConsumer.accept(pathBuilder);
        return method(HttpMethod.TRACE, pathBuilder);
    }

    public @NotNull RestClientRequest trace(@NotNull UriBuilder pathBuilder) {
        return method(HttpMethod.TRACE, pathBuilder);
    }

    public @NotNull RestClientRequest trace(@NotNull URI uri) {
        return method(HttpMethod.TRACE, uri);
    }

    public @NotNull RestClientRequest method(@NotNull HttpMethod method, @NotNull UriBuilder builder) {
        return new RestClientRequest(method, builder.build(), messagingEngine, httpExchange, interceptors);
    }

    public @NotNull RestClientRequest method(@NotNull HttpMethod method, @NotNull URI uri) {
        return new RestClientRequest(method, uri, messagingEngine, httpExchange, interceptors);
    }
}
