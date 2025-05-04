package com.wiredi.web.response;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;

public interface ResponseRenderingEngine {
    Message<HttpResponseMessageDetails> render(
            ResponseEntity responseEntity,
            Message<HttpRequestMessageDetails> requestMessage,
            HttpResponse httpResponse
    );
}
