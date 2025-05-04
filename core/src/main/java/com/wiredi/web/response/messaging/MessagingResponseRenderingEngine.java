package com.wiredi.web.response.messaging;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import com.wiredi.web.response.ResponseEntity;
import com.wiredi.web.response.ResponseRenderingEngine;

public class MessagingResponseRenderingEngine implements ResponseRenderingEngine {

    private final MessagingEngine messagingEngine;

    public MessagingResponseRenderingEngine(
            MessagingEngine messagingEngine
    ) {
        this.messagingEngine = messagingEngine;
    }

    @Override
    public Message<HttpResponseMessageDetails> render(
            ResponseEntity responseEntity,
            Message<HttpRequestMessageDetails> requestMessage,
            HttpResponse httpResponse
    ) {
        // TODO: Respect accepted content types headers
        // TODO: Convert data to content type
        httpResponse.setStatusCode(responseEntity.getStatusCode());
        return messagingEngine.serialize(
                responseEntity.getBody(),
                responseEntity.getHeaders().build(),
                new HttpResponseMessageDetails(requestMessage.details().request(), httpResponse)
        );
    }
}
