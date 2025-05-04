package com.wiredi.web.response.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.web.domain.HttpHeaders;
import com.wiredi.web.domain.MediaType;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class JacksonJsonHttpContentConverter implements HttpContentConverter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean canDeserialize(@NotNull Message<HttpRequestMessageDetails> message, @NotNull Class<?> targetType) {
        List<MessageHeader> contentTypeHeader = message.headers(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader.isEmpty()) {
            return true;
        }

        for (MessageHeader messageHeader : contentTypeHeader) {
            MediaType mediaType = MediaType.resolve(messageHeader.decodeToString());

            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                return mapper.canDeserialize(SimpleType.constructUnsafe(targetType));
            }
        }

        return false;
    }

    @Override
    public @Nullable Object deserialize(@NotNull Message<HttpRequestMessageDetails> message, @NotNull Class<?> targetType) {
        try {
            return mapper.readValue(message.body(), targetType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean canSerialize(
            @NotNull Object payload,
            @NotNull MessageHeaders headers,
            @NotNull HttpResponseMessageDetails messageDetails
    ) {
        List<MessageHeader> contentTypeHeader = headers.allValues(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader.isEmpty()) {
            return true;
        }

        for (MessageHeader messageHeader : contentTypeHeader) {
            MediaType mediaType = MediaType.resolve(messageHeader.decodeToString());

            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                return mapper.canSerialize(payload.getClass());
            }
        }

        return false;

    }

    @Override
    public @Nullable Message<HttpResponseMessageDetails> serialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull HttpResponseMessageDetails messageDetails) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(payload);

            return Message.builder(bytes)
                    .addHeaders(headers)
                    .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withDetails(messageDetails)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
