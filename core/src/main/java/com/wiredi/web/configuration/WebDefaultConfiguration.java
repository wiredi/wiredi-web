package com.wiredi.web.configuration;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.client.HttpExchange;
import com.wiredi.web.client.dispatcher.JavaHttpExchange;
import com.wiredi.web.response.ResponseRenderingEngine;
import com.wiredi.web.response.messaging.MessagingResponseRenderingEngine;

@DefaultConfiguration
public class WebDefaultConfiguration {

    @Provider
    @ConditionalOnMissingBean(type = ResponseRenderingEngine.class)
    public ResponseRenderingEngine messagingResponseRenderingEngine(MessagingEngine messagingEngine) {
        return new MessagingResponseRenderingEngine(messagingEngine);
    }

    @Provider
    @ConditionalOnMissingBean(type = HttpExchange.class)
    public HttpExchange javaHttpExchange() {
        return new JavaHttpExchange();
    }
}
