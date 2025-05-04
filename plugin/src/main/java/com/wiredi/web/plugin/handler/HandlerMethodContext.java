package com.wiredi.web.plugin.handler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.wiredi.compiler.domain.injection.NameContext;

import java.util.ArrayList;
import java.util.List;

public class HandlerMethodContext {

    private final NameContext nameContext = new NameContext();
    private final List<CodeBlock> initializers = new ArrayList<>();
    private final List<String> parameterNames = new ArrayList<>();

    public String nextVariableName(String context) {
        return nameContext.nextName(context);
    }

    public void appendInitializer(CodeBlock initializer) {
        this.initializers.add(initializer);
    }

    public void prependInitializer(CodeBlock initializer) {
        this.initializers.addFirst(initializer);
    }

    public void addParameterName(String parameterName) {
        parameterNames.add(parameterName);
    }

    public void addParameter(String fieldName, CodeBlock initializer) {
        initializers.add(initializer);
        parameterNames.add(fieldName);
    }

    public void addParameter(TypeName typeName, String fieldName, CodeBlock initializer) {
        CodeBlock codeLine = CodeBlock.builder().add("$T $L = ", typeName, fieldName).add(initializer).build();
        addParameter(fieldName, codeLine);
    }

    public List<CodeBlock> initializers() {
        return initializers;
    }

    public List<String> parameterNames() {
        return parameterNames;
    }
}
