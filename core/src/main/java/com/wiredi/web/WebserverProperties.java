package com.wiredi.web;

import java.net.InetSocketAddress;

public record WebserverProperties(
        InetSocketAddress socketAddress,
        String contextPath
) {
}
