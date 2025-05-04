package com.wiredi.web.request;

import com.wiredi.runtime.lang.Ordered;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;

public interface HttpRequestChainElement extends Ordered {

    Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request, HttpRequestChainContext chain);

}
