package com.wiredi.web.sun;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wiredi.annotations.Wire;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.ObjectReference;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.time.Timed;
import com.wiredi.web.Webserver;
import com.wiredi.web.WebserverInstance;
import com.wiredi.web.WebserverProperties;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.request.HttpRequestHandler;
import com.wiredi.web.response.ResponseWriter;
import com.wiredi.web.response.writer.SameThreadResponseWriter;
import jakarta.inject.Named;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

@Wire(proxy = false)
@Named("Sun")
public class SunHttpWebServer implements Webserver {

    private static final Logging logger = Logging.getInstance(SunHttpWebServer.class);
    private final SunHttpProperties httpProperties;
    private final WireRepository wireRepository;
    private final ResponseWriter responseWriter;

    public SunHttpWebServer(
            @NotNull SunHttpProperties httpProperties,
            @NotNull WireRepository wireRepository,
            @NotNull ObjectReference<ResponseWriter> responseWriterRef
            ) {
        this.httpProperties = httpProperties;
        this.wireRepository = wireRepository;
        this.responseWriter = responseWriterRef.getInstance(() -> SameThreadResponseWriter.INSTANCE);
    }
    /**
     * Handles an incoming HTTP request by processing it through the request chain and writing the response.
     *
     * @param exchange The HttpExchange object representing the HTTP request/response pair
     * @param requestChain The chain of request handlers that will process the request
     */
    private void handleInvocation(HttpExchange exchange, HttpRequestHandler requestChain) {
        try {
            final HttpRequest httpRequest = new ExchangeHttpRequest(exchange);
            final Message<HttpRequestMessageDetails> requestMessage = Message.builder(exchange.getRequestBody())
                    .addHeaders(httpRequest.headers())
                    .withDetails(new HttpRequestMessageDetails(httpRequest))
                    .build();

            final Message<HttpResponseMessageDetails> responseMessage = requestChain.handle(requestMessage);
            final HttpResponse httpResponse = responseMessage.details().response();

            // Set headers before sending response headers
            setResponseHeaders(responseMessage, exchange);

            // Use chunked transfer if the content length is unknown
            exchange.sendResponseHeaders(httpResponse.statusCode().value(), contentLength(responseMessage));

            // Write the buffered response body
            this.responseWriter.execute(() -> {
                if (responseMessage.isChunked()) {
                    try (OutputStream os = exchange.getResponseBody()) {
                        responseMessage.writeBodyTo(os);
                        os.flush();
                    } catch (IOException e) {
                        logger.error(() -> "Error writing response for " + httpRequest.path(), e);
                        handleError(e);
                    }
                } else {
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseMessage.body());
                        os.flush();
                    } catch (IOException e) {
                        logger.error(() -> "Error writing response for " + httpRequest.path(), e);
                        handleError(e);
                    }
                }
            });
        } catch (final IOException e) {
            logger.error(() -> "Error while processing request", e);
            handleError(e);
        } finally {
            try {
                exchange.getRequestBody().close();
            } catch (IOException ignored) {
                // Ignore closure exceptions
            }
        }
    }

    /**
     * Determines the appropriate content length for the response message.
     * Returns 0 for chunked transfer encoding, or the actual content length for fixed-size responses.
     *
     * @param message The response message containing the body and metadata
     * @return The content length to use in the response headers (0 for chunked, actual size otherwise)
     */
    private long contentLength(Message<HttpResponseMessageDetails> message) {
        if (message.isChunked()) {
            return 0;
        } else {
            return message.bodySize();
        }
    }

    /**
     * Sets the response headers on the HttpExchange from both the message headers and the HTTP response headers.
     * Headers from both sources are combined in the final response.
     *
     * @param message The response message containing headers to be set
     * @param httpExchange The HttpExchange object on which to set the headers
     */
    private void setResponseHeaders(
            @NotNull final Message<HttpResponseMessageDetails> message,
            @NotNull final HttpExchange httpExchange
    ) {
        final HttpResponse httpResponse = message.details().response();
        final Headers responseHeaders = httpExchange.getResponseHeaders();

        message.headers()
                .forEach((key, values) -> responseHeaders.put(key, values.stream().map(MessageHeader::decodeToString).toList()));

        httpResponse.headers()
                .build()
                .forEach((key, values) -> responseHeaders.put(key, values.stream().map(MessageHeader::decodeToString).toList()));
    }

    /**
     * Handles IO exceptions by delegating to the configured exception handler.
     * If the exception handler throws, wraps the original IOException in an UncheckedIOException.
     *
     * @param e The IOException to handle
     */
    private void handleError(IOException e) {
        try {
            wireRepository.exceptionHandler().handle(e);
        } catch (final Throwable ex) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Starts the HTTP server with the specified properties and request chain.
     * Creates and configures a new Sun HTTP server instance bound to the specified address.
     *
     * @param properties The properties defining server configuration (e.g., port, address)
     * @param requestChain The chain of request handlers that will process incoming requests
     * @return A WebserverInstance representing the running server
     * @throws IOException If the server cannot be created or started
     */
    @Override
    public WebserverInstance start(
            @NotNull final WebserverProperties properties,
            @NotNull final HttpRequestHandler requestChain
    ) throws IOException {
        InetSocketAddress address = properties.socketAddress();
        return Timed.of(() -> {
                    final HttpServer httpServer = HttpServer.create(address, httpProperties.backlog());
                    final SunWebserverInstance webserverInstance = new SunWebserverInstance(httpServer, httpProperties, properties, wireRepository.exceptionHandler());
                    webserverInstance.start(exchange -> this.handleInvocation(exchange, requestChain));
                    return webserverInstance;
                }).then(timed -> logger.info(() -> "Started SunHttpWebServer on port " + properties.socketAddress().getPort() + " in " + timed.time()))
                .value();
    }

    @Override
    public String toString() {
        return "SunHttpWebServer";
    }
}