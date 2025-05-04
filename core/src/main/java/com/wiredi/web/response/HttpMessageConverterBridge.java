package com.wiredi.web.response;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageConverter;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.web.messaging.HttpMessageDetails;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.response.messaging.HttpContentConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Wire(proxy = false)
public class HttpMessageConverterBridge implements MessageConverter<Object, HttpMessageDetails> {

    private final List<HttpContentConverter> converterList;

    public HttpMessageConverterBridge(
            List<HttpContentConverter> converterList
    ) {
        this.converterList = converterList;
    }

    @Override
    public @Nullable Object deserialize(@NotNull Message message, @NotNull Class targetType) {
        if (!(message.details() instanceof HttpRequestMessageDetails)) {
            return null;
        }

        for (HttpContentConverter httpMessageConverter : converterList) {
            if (httpMessageConverter.canDeserialize(message, targetType)) {
                Object deserialized = httpMessageConverter.deserialize(message, targetType);
                if (deserialized != null) {
                    return deserialized;
                }
            }
        }

        return null;
    }

    @Override
    public @Nullable Message<HttpMessageDetails> serialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull HttpMessageDetails messageDetails) {
        if (!(messageDetails instanceof HttpResponseMessageDetails responseDetails)) {
            return null;
        }

        for (HttpContentConverter httpMessageConverter : converterList) {
            if (httpMessageConverter.canSerialize(payload, headers, responseDetails)) {
                Message<?> serialized = httpMessageConverter.serialize(payload, headers, responseDetails);
                if (serialized != null && serialized.details() instanceof HttpMessageDetails) {
                    return (Message<HttpMessageDetails>) serialized;
                }
            }
        }

        return null;
    }
}
