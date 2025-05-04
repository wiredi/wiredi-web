package com.wiredi.web.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public final class HttpMethod implements Comparable<HttpMethod>, Serializable {

    /**
     * The HTTP method {@code OPTIONS}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2">HTTP 1.1, section 9.2</a>
     */
    @NotNull
    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    /**
     * The HTTP method {@code GET}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.3">HTTP 1.1, section 9.3</a>
     */
    @NotNull
    public static final HttpMethod GET = new HttpMethod("GET");
    /**
     * The HTTP method {@code HEAD}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4">HTTP 1.1, section 9.4</a>
     */
    @NotNull
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    /**
     * The HTTP method {@code POST}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.5">HTTP 1.1, section 9.5</a>
     */
    @NotNull
    public static final HttpMethod POST = new HttpMethod("POST");
    /**
     * The HTTP method {@code PUT}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.6">HTTP 1.1, section 9.6</a>
     */
    @NotNull
    public static final HttpMethod PUT = new HttpMethod("PUT");
    /**
     * The HTTP method {@code DELETE}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7">HTTP 1.1, section 9.7</a>
     */
    @NotNull
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    /**
     * The HTTP method {@code TRACE}.
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.8">HTTP 1.1, section 9.8</a>
     */
    @NotNull
    public static final HttpMethod TRACE = new HttpMethod("TRACE");
    /**
     * The HTTP method {@code PATCH}.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5789#section-2">RFC 5789</a>
     */
    @NotNull
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    @Serial
    private static final long serialVersionUID = -70133475680645360L;
    @NotNull
    private static final Map<String, HttpMethod> httpMethods = Map.of(
            OPTIONS.name(), OPTIONS,
            GET.name(), GET,
            HEAD.name(), HEAD,
            POST.name(), POST,
            PUT.name(), PUT,
            DELETE.name(), DELETE,
            TRACE.name(), TRACE,
            PATCH.name(), PATCH
    );

    @NotNull
    private final String name;

    public HttpMethod(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public static HttpMethod valueOf(String name) {
        HttpMethod existingMethod = httpMethods.get(name);
        if (existingMethod != null) {
            return existingMethod;
        }
        return new HttpMethod(name);
    }

    public static Collection<HttpMethod> values() {
        return httpMethods.values();
    }

    @NotNull
    public String name() {
        return this.name;
    }

    public boolean matches(@NotNull String method) {
        return this.name.equals(method);
    }


    @Override
    public int compareTo(@NotNull HttpMethod other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof HttpMethod otherMethod) {
            return this.name.equals(otherMethod.name);
        }
        if (o instanceof HttpMethods otherMethod) {
            return this.name.equals(otherMethod.httpMethod().name);
        }
        return false;
    }

    @Override
    @NotNull
    public String toString() {
        return this.name;
    }
}
