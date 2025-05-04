package com.wiredi.web.response.messaging;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HttpContentConverter {

    boolean canDeserialize(@NotNull Message<HttpRequestMessageDetails> message, @NotNull Class<?> targetType);

    @Nullable Object deserialize(@NotNull Message<HttpRequestMessageDetails> message, @NotNull Class<?> targetType);

    boolean canSerialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull HttpResponseMessageDetails messageDetails);

    @Nullable Message<HttpResponseMessageDetails> serialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull HttpResponseMessageDetails messageDetails);

}
