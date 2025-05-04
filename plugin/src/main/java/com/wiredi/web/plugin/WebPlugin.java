package com.wiredi.web.plugin;

import com.google.auto.service.AutoService;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.domain.entities.IdentifiableProviderEntity;
import com.wiredi.compiler.logger.Logger;
import com.wiredi.compiler.processor.lang.utils.TypeElements;
import com.wiredi.compiler.processor.plugins.CompilerEntityPlugin;
import com.wiredi.compiler.repository.CompilerRepository;
import com.wiredi.web.api.RestRequestHandler;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

@AutoService(CompilerEntityPlugin.class)
public class WebPlugin implements CompilerEntityPlugin {

    @Inject
    private Types types;

    @Inject
    private TypeElements typeElements;

    @Inject
    private CompilerRepository compilerRepository;

    @Inject
    private VariableContext variableContext;

    private static final Logger logger = Logger.get(WebPlugin.class);

    @Override
    public void initialize() {
        logger.info(() -> "Initializing WebPlugin");
    }

    @Override
    public void handle(@NotNull IdentifiableProviderEntity entity) {
        TypeMirror rootType = entity.rootType();
        TypeElement element = (TypeElement) types.asElement(rootType);

        typeElements.declaredMethodsOf(element)
                .forEach(this::handle);
    }

    private void handle(ExecutableElement method) {
        logger.info(() -> "Checking for RestRequestHandler annotation on " + method.getEnclosingElement().getSimpleName() + "." + method.getSimpleName());
        Annotations.getAnnotation(method, RestRequestHandler.class).ifPresent(annotation -> {
            logger.info(() -> "Found RequestHandler annotation on method " + method.getEnclosingElement().getSimpleName() + "." + method.getSimpleName());
            ControllerInvocationHandlerClassEntity entity = constructHandlerImplementation(method, annotation);
            compilerRepository.save(entity);
        });
    }

    private ControllerInvocationHandlerClassEntity constructHandlerImplementation(ExecutableElement method, RestRequestHandler annotation) {
        return new ControllerInvocationHandlerClassEntity(method)
                .addConstructor()
                .setHttpMethod(annotation.method().httpMethod())
                .setPathPattern(annotation.path())
                .setInvocationMethod(variableContext, annotation);
    }
}
