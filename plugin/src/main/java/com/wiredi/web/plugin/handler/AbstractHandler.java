package com.wiredi.web.plugin.handler;

import com.wiredi.compiler.domain.injection.NameContext;

import javax.lang.model.element.VariableElement;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractHandler {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    protected void tryInitialize(Runnable runnable) {
        if (!initialized.get()) {
            runnable.run();
            initialized.set(true);
        }
    }

    public abstract void handle(VariableElement parameter);
}
