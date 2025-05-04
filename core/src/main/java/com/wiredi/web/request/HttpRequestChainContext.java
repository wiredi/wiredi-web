package com.wiredi.web.request;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;

public interface HttpRequestChainContext {

    boolean hasNext();

    Message<HttpResponseMessageDetails> next(Message<HttpRequestMessageDetails> request);

}
