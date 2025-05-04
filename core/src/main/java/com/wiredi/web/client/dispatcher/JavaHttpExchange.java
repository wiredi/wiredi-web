package com.wiredi.web.client.dispatcher;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.messages.InputStreamMessage;
import com.wiredi.web.client.HttpExchange;
import com.wiredi.web.client.HttpExchangeRequest;
import com.wiredi.web.client.HttpExchangeResponse;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.domain.HttpStatusCode;
import com.wiredi.web.messaging.RestClientRequestDetails;
import com.wiredi.web.messaging.RestClientResponseDetails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaHttpExchange implements HttpExchange {

    @Override
    public HttpExchangeResponse exchange(HttpExchangeRequest request) {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = createRequest(request.method(), request.uri(), request.message());
            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            InputStreamMessage.Builder<RestClientResponseDetails> message = Message.builder(response.body())
                    .withDetails(new RestClientResponseDetails());
            response.headers().map()
                    .forEach((key, values) -> values.forEach(value -> message.addHeader(key, value)));
            InputStreamMessage<RestClientResponseDetails> responseMessage = message.build();

            return new HttpExchangeResponse(HttpStatusCode.resolve(response.statusCode()), responseMessage);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest createRequest(HttpMethod httpMethod, URI uri, Message<RestClientRequestDetails> request) {
        HttpRequest.BodyPublisher bodyPublisher;
        if (request.isChunked()) {
            bodyPublisher = HttpRequest.BodyPublishers.ofInputStream(request::inputStream);
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(request.body());
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .method(httpMethod.name(), bodyPublisher);

        request.headers()
                .forEach((key, values) -> values.forEach(value -> requestBuilder.header(key, value.decodeToString())));


        return requestBuilder.build();
    }
}
