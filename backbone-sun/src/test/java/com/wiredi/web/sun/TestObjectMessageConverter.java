package com.wiredi.web.sun;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageConverter;
import com.wiredi.runtime.messaging.MessageDetails;
import com.wiredi.runtime.types.Bytes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Wire
public class TestObjectMessageConverter implements MessageConverter<TestObject, MessageDetails> {
    @Override
    public boolean canDeserialize(@NotNull Message<?> message, @NotNull Class<?> targetType) {
        return targetType == TestObject.class;
    }

    @Override
    public @Nullable TestObject deserialize(@NotNull Message<MessageDetails> message, @NotNull Class<TestObject> targetType) {
        return new TestObject(Bytes.toString(message.body()));
    }

    @Override
    public boolean canSerialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull MessageDetails messageDetails) {
        return payload.getClass() == TestObject.class;
    }

    @Override
    public @Nullable Message<MessageDetails> serialize(@NotNull Object payload, @NotNull MessageHeaders headers, @NotNull MessageDetails messageDetails) {
        return Message.builder(((TestObject) payload).value().getBytes())
                .addHeaders(headers)
                .withDetails(messageDetails)
                .build();
    }
}
