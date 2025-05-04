package com.wiredi.web.sun;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.ExceptionHandlerContext;
import com.wiredi.runtime.time.Timed;
import com.wiredi.runtime.values.Value;
import com.wiredi.web.WebserverProperties;
import com.wiredi.web.WebserverInstance;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class SunWebserverInstance implements WebserverInstance {

    private final HttpServer httpServer;
    private final Value<HttpContext> contextValue = Value.empty();
    private final SunHttpProperties httpProperties;
    private final WebserverProperties webserverProperties;
    private final ExceptionHandlerContext exceptionHandler;
    private static final Logging logger = Logging.getInstance(SunWebserverInstance.class);

    public SunWebserverInstance(
            HttpServer httpServer,
            SunHttpProperties httpProperties,
            WebserverProperties webserverProperties,
            ExceptionHandlerContext exceptionHandler
    ) {
        this.httpServer = httpServer;
        this.httpProperties = httpProperties;
        this.webserverProperties = webserverProperties;
        this.exceptionHandler = exceptionHandler;
    }

    void start(Consumer<HttpExchange> handleInvocation) {
        InetSocketAddress address = webserverProperties.socketAddress();
        logger.debug("Starting Sun HttpServer on port " + address.getPort());
        HttpContext context = httpServer.createContext(webserverProperties.contextPath(), exchange -> {
            try {
                handleInvocation.accept(exchange);
            } catch (final Throwable throwable) {
                try {
                    exceptionHandler.handle(throwable);
                } catch (Throwable e) {
                    logger.error(() -> "Internal error while handling request to SunHttpWebServer", e);
                }
            }
        });
        this.contextValue.set(context);
        httpServer.start();
    }

    @Override
    public void stop() {
        logger.debug("Shutting down SunHttpWebServer");
        Timed.of(() -> httpServer.stop(httpProperties.shutdownTimeout().toSecondsPart()))
                .then(timed -> logger.info(() -> "Stopped SunHttpWebServer on port " + webserverProperties.socketAddress().getPort() + " shutdown in " + timed));

    }
}
