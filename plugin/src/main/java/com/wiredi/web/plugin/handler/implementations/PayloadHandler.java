package com.wiredi.web.plugin.handler.implementations;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.plugin.VariableContext;
import com.wiredi.web.plugin.handler.AbstractHandler;
import com.wiredi.web.plugin.handler.HandlerMethodContext;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class PayloadHandler extends AbstractHandler {

    private final HandlerMethodContext context;
    private final VariableContext variableContext;
    private TypeName payloadType;

    public PayloadHandler(HandlerMethodContext context, VariableContext variableContext) {
        this.context = context;
        this.variableContext = variableContext;
    }

    public void handle(VariableElement element) {
        TypeName targetPayloadType = ClassName.get(element.asType());
        if (payloadType != null && payloadType != targetPayloadType) {
            throw new IllegalStateException("Conflicting payload types: The payload is initialized as " + payloadType + " but tried to access it as " + targetPayloadType);
        }

        tryInitialize(() -> {
            TypeMirror messagePayloadType;
            if (variableContext.isMessage(element)) {
                DeclaredType genericType = (DeclaredType) element.asType();
                messagePayloadType = genericType.getTypeArguments().getFirst();
            } else {
                messagePayloadType = element.asType();
            }
            this.payloadType = ClassName.get(messagePayloadType);

            context.prependInitializer(
                    CodeBlock.builder()
                            .add("final $T<$T, $T> message = messagingEngine.get().deserialize(requestState.httpRequest().message(), $T.class)", Message.class, TypeName.get(messagePayloadType), HttpRequestMessageDetails.class, TypeName.get(messagePayloadType))
                            .build()
            );
        });

        context.addParameter(TypeName.get(element.asType()), context.nextVariableName("payload"), CodeBlock.of("message.getBody()"));
    }
}
