package com.wiredi.web.sun;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

@PropertyBinding(
        prefix = "com.wiredi.http-server.sun"
)
public record SunHttpProperties(
        @Nullable String host,
        @Property(defaultValue = "0") int backlog,
        @Property(defaultValue = "PT0S") Duration shutdownTimeout
) {

    private static final String DEFAULT_HOST = "localhost";

    @Override
    @NotNull
    public String host() {
        return Objects.requireNonNullElse(host, DEFAULT_HOST);
    }

    @Override
    @NotNull
    public Duration shutdownTimeout() {
        return shutdownTimeout;
    }
}
