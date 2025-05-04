package com.wiredi.web.client;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.RestClientRequestDetails;

import java.net.URI;

public record HttpExchangeRequest(
        HttpMethod method,
        URI uri,
        Message<RestClientRequestDetails> message
) {
}
