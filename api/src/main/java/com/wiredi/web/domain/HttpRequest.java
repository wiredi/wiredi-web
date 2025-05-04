package com.wiredi.web.domain;

import com.wiredi.runtime.messaging.MessageHeaders;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class HttpRequest {

    @NotNull
    private final MessageHeaders headers;
    @NotNull
    private final RequestStatusLine statusLine;
    @NotNull
    private final String path;
    @NotNull
    private InputStream bodyInputStream;

    protected HttpRequest(
            @NotNull
            RequestStatusLine statusLine,
            @NotNull
            MessageHeaders headers,
            @NotNull
            InputStream bodyInputStream,
            @NotNull
            String path
    ) {
        this.headers = headers;
        this.bodyInputStream = bodyInputStream;
        this.statusLine = statusLine;
        this.path = path;
    }

    private void updateBodyInputStream(@NotNull InputStream bodyInputStream) {
        this.bodyInputStream = bodyInputStream;
    }

    @NotNull
    public MessageHeaders headers() {
        return headers;
    }

    @NotNull
    public InputStream bodyStream() {
        return bodyInputStream;
    }

    @NotNull
    public RequestStatusLine requestStatusLine() {
        return statusLine;
    }

    public String path() {
        return path;
    }

    public Map<String, List<String>> requestParameters() {
        return statusLine.requestParameters();
    }

    public List<MediaType> acceptedMediaTypes() {
        return headers.allValues(HttpHeaders.ACCEPT)
                .stream()
                .map(it -> MediaType.resolve(it.decodeToString()))
                .toList();
    }

    public abstract HttpResponse newResponse();
}
