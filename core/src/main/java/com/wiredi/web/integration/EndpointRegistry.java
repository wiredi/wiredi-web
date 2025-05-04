package com.wiredi.web.integration;

import com.wiredi.web.AntPathMatcher;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.domain.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EndpointRegistry<T> {

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final Map<String, Map<HttpMethod, T>> entries = new HashMap<>();

    @Nullable
    public T get(HttpEndpoint endpoint) {
        return tryFindMatcingEndpoint(endpoint);
    }

    @Nullable
    public T put(@NotNull HttpEndpoint s, @NotNull T t) {
        return entries.computeIfAbsent(s.pattern(), p -> new HashMap<>()).put(s.method(), t);
    }

    @Nullable
    private T tryFindMatcingEndpoint(HttpEndpoint endpoint) {
        List<String> matchingKeys = entries.keySet().stream()
                .filter(it -> matcher.matches(it, endpoint.pattern()))
                .sorted(matcher.getPatternComparator(endpoint.pattern()))
                .toList();


        for (String path : matchingKeys) {
            Map<HttpMethod, T> methodMap = entries.get(path);
            T t = methodMap.get(endpoint.method());
            if (t != null) {
                return t;
            }
        }

        return null;
    }

    public int size() {
        return entries.size();
    }

    @Nullable
    public T computeIfAbsent(@NotNull HttpEndpoint endpoint, Supplier<T> supplier) {
        return entries.computeIfAbsent(endpoint.pattern(), path -> new HashMap<>()).computeIfAbsent(endpoint.method(), (method) -> supplier.get());
    }

    @Nullable
    public T computeIfAbsent(@NotNull HttpEndpoint endpoint, Function<HttpEndpoint, T> function) {
        return entries.computeIfAbsent(endpoint.pattern(), path -> new HashMap<>()).computeIfAbsent(endpoint.method(), (method) -> function.apply(endpoint));
    }
}
