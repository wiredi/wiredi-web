package com.wiredi.web.plugin.handler.implementations;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.web.api.PathVariable;
import com.wiredi.web.api.RequestParameter;
import com.wiredi.web.plugin.handler.AbstractHandler;
import com.wiredi.web.plugin.handler.HandlerMethodContext;

import javax.lang.model.element.VariableElement;
import java.util.Map;

public class PathVariableHandler extends AbstractHandler {

    private final HandlerMethodContext handlerMethodContext;

    public PathVariableHandler(
            HandlerMethodContext handlerMethodContext
    ) {
        this.handlerMethodContext = handlerMethodContext;
    }


    public void handle(VariableElement parameter) {
        tryInitialize(() -> {
            handlerMethodContext.prependInitializer(
                    CodeBlock.builder()
                            .add("final $T pathVariables = requestState.requestPathVariables(pathPattern())", ParameterizedTypeName.get(Map.class, String.class, String.class))
                            .build()
            );
        });


        String variableName = handlerMethodContext.nextVariableName("pathVariable");
        String pathVariableName = Annotations.getAnnotation(parameter, PathVariable.class)
                .map(annotation -> {
                    if (annotation.name().isBlank()) {
                        return parameter.getSimpleName().toString();
                    } else {
                        return annotation.name();
                    }
                })
                .orElse(variableName);
        handlerMethodContext.addParameter(
                TypeName.get(parameter.asType()),
                variableName,
                CodeBlock.of("typeMapper.get().parse($T.class, pathVariables.get($S))", TypeName.get(parameter.asType()), pathVariableName)
        );
    }
}
