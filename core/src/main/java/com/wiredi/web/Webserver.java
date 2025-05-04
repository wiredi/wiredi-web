package com.wiredi.web;

import com.wiredi.web.request.HttpRequestHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Webserver {

    WebserverInstance start(
            @NotNull final WebserverProperties properties,
            @NotNull final HttpRequestHandler httpRequestChain
    ) throws IOException;

}
