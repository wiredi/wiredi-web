package com.wiredi.web.client;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpStatusCode;
import com.wiredi.web.messaging.RestClientResponseDetails;

public record HttpExchangeResponse(
        HttpStatusCode statusCode,
        Message<RestClientResponseDetails> message
) {
}
