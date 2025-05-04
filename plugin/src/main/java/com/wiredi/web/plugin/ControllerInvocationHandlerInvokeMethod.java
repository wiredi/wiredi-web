package com.wiredi.web.plugin;

import com.squareup.javapoet.*;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.domain.ClassEntity;
import com.wiredi.compiler.domain.entities.methods.StandaloneMethodFactory;
import com.wiredi.compiler.domain.injection.NameContext;
import com.wiredi.compiler.errors.ProcessingException;
import com.wiredi.compiler.logger.Logger;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.annotations.Header;
import com.wiredi.runtime.messaging.annotations.Payload;
import com.wiredi.web.api.PathVariable;
import com.wiredi.web.api.RequestParameter;
import com.wiredi.web.api.RestRequestHandler;
import com.wiredi.web.domain.HttpHeaders;
import com.wiredi.web.domain.MediaType;
import com.wiredi.web.domain.MediaTypes;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.plugin.handler.HandlerMethodContext;
import com.wiredi.web.plugin.handler.implementations.HeaderHandler;
import com.wiredi.web.plugin.handler.implementations.PathVariableHandler;
import com.wiredi.web.plugin.handler.implementations.PayloadHandler;
import com.wiredi.web.plugin.handler.implementations.RequestParameterHandler;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ControllerInvocationHandlerInvokeMethod implements StandaloneMethodFactory {

    private static final Logger logger = Logger.get(ControllerInvocationHandlerInvokeMethod.class);
    private final ExecutableElement executableElement;
    private final RestRequestHandler annotation;
    private final VariableContext variableContext;

    public ControllerInvocationHandlerInvokeMethod(ExecutableElement executableElement, RestRequestHandler annotation, VariableContext variableContext) {
        this.executableElement = executableElement;
        this.annotation = annotation;
        this.variableContext = variableContext;
    }

    @Override
    public String methodName() {
        return "invoke";
    }

    @Override
    public void append(MethodSpec.Builder builder, ClassEntity<?> entity) {
        builder.addAnnotation(Override.class).addAnnotation(NotNull.class)
                // TODO: Use Message<WebServerRequestHttpMessageDetails> message parameter instead of requestState
//                .addParameter(
//                        ParameterSpec.builder(RequestState.class, "requestState")
//                                .addAnnotation(NotNull.class)
//                                .build()
//                )
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(ResponseEntity.class);

        MediaTypes mediaType = annotation.produces();
        CodeBlock.Builder methodBody = CodeBlock.builder()
                .addStatement("requestState.httpResponse().headers().add($T.CONTENT_TYPE, $T.$L_VALUE)", HttpHeaders.class, MediaType.class, mediaType.name());
        List<String> parameters = constructMethodParameters(methodBody, executableElement, entity);

        if (executableElement.getReturnType() instanceof NoType) {
            methodBody.addStatement("handler.$L($L)", executableElement.getSimpleName(), String.join(", ", parameters));
            methodBody.addStatement("return $T.ok()", ResponseEntity.class);
        } else {
            if (variableContext.isResponseEntity(executableElement.getReturnType())) {
                methodBody.addStatement("return handler.$L($L)", executableElement.getSimpleName(), String.join(", ", parameters));
            } else {
                methodBody.addStatement("$T response = handler.$L($L)", TypeName.get(executableElement.getReturnType()), executableElement.getSimpleName(), String.join(", ", parameters));
                methodBody.addStatement("return $T.ok(response)", ResponseEntity.class);
            }
        }

        builder.addCode(methodBody.build());
    }

    private List<String> constructMethodParameters(
            CodeBlock.Builder builder,
            ExecutableElement executableElement,
            ClassEntity<?> entity
    ) {
        HandlerMethodContext context = new HandlerMethodContext();
        RequestParameterHandler requestParameterHandler = new RequestParameterHandler(context);
        PathVariableHandler pathVariableHandler = new PathVariableHandler(context);
        PayloadHandler payloadHandler = new PayloadHandler(context, variableContext);
        HeaderHandler headerHandler = new HeaderHandler(context);

        NameContext nameContext = new NameContext();
        final AtomicBoolean messageDeserialized = new AtomicBoolean();
        final AtomicReference<TypeMirror> payloadType = new AtomicReference<>();
        BiConsumer<Element, TypeMirror> checkPayloadType = (element, typeMirror) -> {
            if (payloadType.get() != null && !variableContext.isOfType(payloadType.get(), typeMirror)) {
                throw new ProcessingException(element, "The element " + element + " does not match the declared payload type. Expected " + payloadType.get() + " but got " + typeMirror);
            } else {
                payloadType.set(typeMirror);
            }
        };
        Consumer<Element> initializeMessage = (element) -> {
            if (!messageDeserialized.get()) {
                TypeMirror messagePayloadType;
                if (variableContext.isMessage(element)) {
                    DeclaredType genericType = (DeclaredType) element.asType();
                    messagePayloadType = genericType.getTypeArguments().getFirst();
                } else {
                    messagePayloadType = element.asType();
                }

                context.prependInitializer(
                        CodeBlock.builder()
                                .add("final $T<$T, $T> message = messagingEngine.get().deserialize(requestState.httpRequest().message(), $T.class)", Message.class, TypeName.get(messagePayloadType), HttpRequestMessageDetails.class, TypeName.get(messagePayloadType))
                                .build()
                );
                checkPayloadType.accept(element, messagePayloadType);
                messageDeserialized.set(true);
            }
        };

        executableElement.getParameters().forEach(parameter -> {
            if (Annotations.isAnnotatedWith(parameter, Payload.class)) {
                payloadHandler.handle(parameter);
            } else if (Annotations.isAnnotatedWith(parameter, Header.class)) {
                headerHandler.handle(parameter);
            } else if (Annotations.isAnnotatedWith(parameter, PathVariable.class)) {
                pathVariableHandler.handle(parameter);
            } else if (Annotations.isAnnotatedWith(parameter, RequestParameter.class)) {
                requestParameterHandler.handle(parameter);
            } else {
                if (variableContext.isHttpRequest(parameter)) {
                    context.addParameterName("requestState.httpRequest()");
                } else if (variableContext.isHttpResponse(parameter)) {
                    context.addParameterName("requestState.httpResponse()");
                } else if (variableContext.isHeaders(parameter)) {
                    context.addParameterName("requestState.httpRequest().headers()");
                } else if (variableContext.isMessage(parameter)) {
                    String variableName = context.nextVariableName("message");
                    DeclaredType genericType = (DeclaredType) parameter.asType();
                    TypeMirror genericPayloadType = genericType.getTypeArguments().getFirst();
                    TypeMirror genericDetailsType = genericType.getTypeArguments().getLast();
                    // TODO: Check that details type can be correctly assigned

                    context.addParameter(
                            ParameterizedTypeName.get(
                                    ClassName.get(Message.class),
                                    ClassName.get(genericPayloadType),
                                    ClassName.get(genericDetailsType)
                            ),
                            variableName,
                            CodeBlock.of("messagingEngine.get().deserialize(requestState.httpRequest().message(), $T.class)", TypeName.get(genericPayloadType))
                    );
                } else {
                    String variableName = nameContext.nextName("parameter");
                    context.appendInitializer(
                            CodeBlock.builder()
                                    .add("final $T $L = wireRepository.get($T.class)", TypeName.get(parameter.asType()), variableName, TypeName.get(parameter.asType()))
                                    .build()
                    );
                    context.addParameterName(variableName);
                }
            }
        });

        if (!context.initializers().isEmpty()) {
            builder.addStatement(
                    CodeBlock.join(context.initializers(), ";" + System.lineSeparator())
            );
        }
        return context.parameterNames();
    }
}
