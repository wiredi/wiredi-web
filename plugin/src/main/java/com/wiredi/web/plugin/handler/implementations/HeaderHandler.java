package com.wiredi.web.plugin.handler.implementations;

import com.squareup.javapoet.CodeBlock;
import com.wiredi.web.plugin.handler.AbstractHandler;
import com.wiredi.web.plugin.handler.HandlerMethodContext;

import javax.lang.model.element.VariableElement;

public class HeaderHandler extends AbstractHandler {

    private final HandlerMethodContext context;

    public HeaderHandler(HandlerMethodContext context) {
        this.context = context;
    }

    @Override
    public void handle(VariableElement parameter) {
        context.appendInitializer(
                CodeBlock.builder()
                        .add("// TODO: Support @Header annotation")
                        .build()
        );
    }
}
