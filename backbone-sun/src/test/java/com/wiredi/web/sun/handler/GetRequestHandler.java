package com.wiredi.web.sun.handler;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.tests.async.AsyncResult;
import com.wiredi.tests.async.AsyncResultList;
import com.wiredi.web.domain.HttpEndpoint;
import com.wiredi.web.PathVariables;
import com.wiredi.web.domain.HttpMethod;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.request.HandlerMethod;
import com.wiredi.web.response.ResponseEntity;
import com.wiredi.web.sun.TestObject;
import org.jetbrains.annotations.NotNull;

@Wire(proxy = false)
public class GetRequestHandler implements HandlerMethod {

    public static final String PATH = "/groups/{groupId}/users/{userId}";
    @NotNull
    private final AsyncResultList<Message<HttpRequestMessageDetails>> invocations = AsyncResult.list();
    @NotNull
    private final MessagingEngine messagingEngine;

    public GetRequestHandler(
            @NotNull MessagingEngine messagingEngine
    ) {
        this.messagingEngine = messagingEngine;
    }

    @Override
    public @NotNull ResponseEntity invoke(@NotNull Message<HttpRequestMessageDetails> message) {
        invocations.add(message);
        PathVariables pathVariables = new PathVariables(endpoint().pattern(), message.details().request().path());

        // Deserialize the body
        TestObject requestObject = messagingEngine.deserialize(message, TestObject.class);
        // Invoke handler
        TestObject responseObject = new TestObject("User " + pathVariables.get("userId") + " of group " + pathVariables.get("groupId") + " - Response: " + requestObject.value());

        return ResponseEntity.ok(responseObject);
    }

    @Override
    public @NotNull HttpEndpoint endpoint() {
        return new HttpEndpoint(PATH, HttpMethod.GET);
    }

    public AsyncResultList<Message<HttpRequestMessageDetails>> getInvocations() {
        return invocations;
    }
}
