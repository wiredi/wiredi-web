package com.wiredi.web.plugin.handler.implementations;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.web.api.RequestParameter;
import com.wiredi.web.plugin.handler.AbstractHandler;
import com.wiredi.web.plugin.handler.HandlerMethodContext;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;

public class RequestParameterHandler extends AbstractHandler {

    private final HandlerMethodContext handlerMethodContext;

    public RequestParameterHandler(
            HandlerMethodContext handlerMethodContext
    ) {
        this.handlerMethodContext = handlerMethodContext;
    }


    public void handle(VariableElement parameter) {
        tryInitialize(() -> {
            handlerMethodContext.prependInitializer(
                    CodeBlock.builder()
                            .add("final $T requestParameters = requestState.httpRequest().requestParameters()", ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ParameterizedTypeName.get(List.class, String.class)))
                            .build()
            );
        });

        String variableName = handlerMethodContext.nextVariableName("requestParameter");
        String pathVariableName = Annotations.getAnnotation(parameter, RequestParameter.class)
                .map(annotation -> {
                    if (annotation.name().isBlank()) {
                        return parameter.getSimpleName().toString();
                    } else {
                        return annotation.name();
                    }
                })
                .orElse(variableName);
        // TODO: Support list of parameters
        handlerMethodContext.addParameter(TypeName.get(parameter.asType()), variableName, CodeBlock.builder()
                        .add("typeMapper.get().parse($T.class, requestParameters.get($S).get(0))", TypeName.get(parameter.asType()), pathVariableName)
                .build());
    }

}
