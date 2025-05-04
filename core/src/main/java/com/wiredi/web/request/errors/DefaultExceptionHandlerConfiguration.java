package com.wiredi.web.request.errors;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.AutoConfiguration;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;

@AutoConfiguration
public class DefaultExceptionHandlerConfiguration {

    @Provider
    @ConditionalOnMissingBean(type = DefaultExceptionHandler.class)
    public DefaultExceptionHandler defaultExceptionHandler() {
        return new Default500ExceptionHandler();
    }
}
