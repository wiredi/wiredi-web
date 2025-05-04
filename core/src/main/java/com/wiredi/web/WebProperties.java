package com.wiredi.web;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

@PropertyBinding(prefix = "wiredi.web")
public record WebProperties(
        @Property(defaultValue = "8448")
        int port,
        @Property(defaultValue = "/")
        String contextPath,
        @Property(defaultValue = "application/json")
        String defaultMediaType
) {

    @NotNull
    public InetSocketAddress socketAddress() {
        return new InetSocketAddress(this.port);
    }
}
