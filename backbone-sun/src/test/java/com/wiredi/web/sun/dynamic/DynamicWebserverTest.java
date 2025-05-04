package com.wiredi.web.sun.dynamic;

import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.WebserverProperties;
import com.wiredi.web.Webserver;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DynamicWebserverTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Arrange
        WireRepository wireRepository = WireRepository.open();
        WebserverProperties properties = new WebserverProperties(new InetSocketAddress("localhost", 8282), "/");
        Logger logger = LoggerFactory.getLogger(DynamicWebserverTest.class);

        // Act
        Webserver webserver = wireRepository.get(Webserver.class);

        // Assert
        webserver.start(properties, message -> {
            logger.info("Received Request");
            return Message.builder("Hello World!".getBytes())
                    .withDetails(new HttpResponseMessageDetails(message.details().request(), message.details().request().newResponse()))
                    .build();
        });
        Thread.currentThread().join();
    }
}
