package com.wiredi.web.plugin;

import com.squareup.javapoet.*;
import com.wiredi.annotations.Wire;
import com.wiredi.compiler.domain.AbstractClassEntity;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.types.TypeMapper;
import com.wiredi.runtime.values.Value;
import com.wiredi.web.api.RestRequestHandler;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseRenderingEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class ControllerInvocationHandlerClassEntity extends AbstractClassEntity<ControllerInvocationHandlerClassEntity> {

    private static final String HTTP_METHOD_FIELD_NAME = "HTTP_METHOD";
    private final ExecutableElement executableElement;

    public ControllerInvocationHandlerClassEntity(
            @NotNull ExecutableElement executableElement
    ) {
        super(executableElement, executableElement.getEnclosingElement().asType(), constructClassName(executableElement));
        this.executableElement = executableElement;
    }

    private static String constructClassName(ExecutableElement executableElement) {
        return executableElement.getEnclosingElement().getSimpleName().toString() + "$" + executableElement.getSimpleName() + "$ControllerInvocationHandler";
    }

    @Override
    protected TypeSpec.Builder createBuilder(TypeMirror type) {
        return TypeSpec.classBuilder(className())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(HandlerMethod.class)
                .addAnnotation(AnnotationSpec.builder(Wire.class)
                        .addMember("proxy", "$L", false)
                        .build());
    }

    public ControllerInvocationHandlerClassEntity addConstructor() {
        addField(TypeName.get(executableElement.getEnclosingElement().asType()), "handler", field -> field.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addField(WireRepository.class, "wireRepository", field -> field.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addField(ParameterizedTypeName.get(Value.class, ResponseRenderingEngine.class), "renderingEngine", field -> field.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addField(ParameterizedTypeName.get(Value.class, MessagingEngine.class), "messagingEngine", field -> field.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addField(ParameterizedTypeName.get(Value.class, TypeMapper.class), "typeMapper", field -> field.addModifiers(Modifier.PRIVATE, Modifier.FINAL));

        builder.addMethod(
                MethodSpec.constructorBuilder()
                        .addParameter(TypeName.get(executableElement.getEnclosingElement().asType()), "handler")
                        .addParameter(WireRepository.class, "wireRepository")
                        .addParameter(MessagingEngine.class, "messagingEngine")
                        .addStatement("this.handler = handler")
                        .addStatement("this.wireRepository = wireRepository")
                        .addStatement("this.renderingEngine = $T.lazy(() -> wireRepository.get($T.class))", Value.class, ResponseRenderingEngine.class)
                        .addStatement("this.messagingEngine = $T.lazy(() -> wireRepository.get($T.class))", Value.class, MessagingEngine.class)
                        .addStatement("this.typeMapper = $T.lazy(() -> wireRepository.get($T.class))", Value.class, TypeMapper.class)
                        .build()
        );

        return this;
    }

    public ControllerInvocationHandlerClassEntity setInvocationMethod(VariableContext variableContext, RestRequestHandler restRequestHandler) {
        addMethod(new ControllerInvocationHandlerInvokeMethod(executableElement, restRequestHandler, variableContext));
        return this;
    }

    public ControllerInvocationHandlerClassEntity setPathPattern(String name) {
        addMethod("pathPattern",
                m -> m.addAnnotation(Override.class).addAnnotation(NotNull.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .returns(String.class)
                        .addStatement("return $S", name)
        );

        return this;
    }

    public ControllerInvocationHandlerClassEntity setHttpMethod(HttpMethod method) {
        addField(HttpMethod.class, HTTP_METHOD_FIELD_NAME, f -> f.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T($S)", HttpMethod.class, method.name()));
        addMethod(
                "httpMethod",
                m -> m.addAnnotation(Override.class).addAnnotation(Nullable.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .returns(HttpMethod.class)
                        .addStatement("return $L", HTTP_METHOD_FIELD_NAME)
        );

        return this;
    }
}
