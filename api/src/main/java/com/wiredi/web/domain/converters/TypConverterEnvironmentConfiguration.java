package com.wiredi.web.domain.converters;

import com.google.auto.service.AutoService;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.Environment;
import com.wiredi.runtime.environment.EnvironmentConfiguration;
import com.wiredi.web.domain.HttpStatusCode;
import com.wiredi.web.domain.MediaType;
import org.jetbrains.annotations.NotNull;

@AutoService(EnvironmentConfiguration.class)
public final class TypConverterEnvironmentConfiguration implements EnvironmentConfiguration {

    private static final Logging logger = Logging.getInstance(TypConverterEnvironmentConfiguration.class);

    @Override
    public void configure(@NotNull Environment environment) {
        environment.typeMapper().setTypeConverter(MediaTypePropertyConverter.INSTANCE).forAllSupportedTypes();
        environment.typeMapper().setTypeConverter(StatusCodePropertyConverter.INSTANCE).forAllSupportedTypes();
        logger.debug(() -> "Setup type converters for http domain components");
    }
}
