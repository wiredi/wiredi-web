package com.wiredi.web;

import com.wiredi.annotations.properties.PropertyBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;

@PropertyBinding(
        prefix = "com.wiredi.http-server.sun"
)
public record HttpProperties(
        @Nullable String host,
        String contextPath,
        int port,
        int backlog,
        Duration shutdownTimeout
) {

    private static final String DEFAULT_HOST = "localhost";

    @Override
    @NotNull
    public String host() {
        return Objects.requireNonNullElse(host, DEFAULT_HOST);
    }

    @Override
    @NotNull
    public String contextPath() {
        return contextPath;
    }

    @Override
    @NotNull
    public Duration shutdownTimeout() {
        return shutdownTimeout;
    }

    @NotNull
    public InetSocketAddress socketAddress() {
        if (this.host == null || this.host.isBlank() || this.host.equals("__NULL__")) {
            return new InetSocketAddress(this.port);
        } else {
            return InetSocketAddress.createUnresolved(this.host, this.port);
        }
    }
}
