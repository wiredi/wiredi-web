package com.wiredi.web.request;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.web.domain.*;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestChainTest {

    private HttpRequestChainElement firstHandler;
    private HttpRequestChainElement secondHandler;
    private HttpRequestChainElement thirdHandler;
    private Message<HttpRequestMessageDetails> request;

    @BeforeEach
    void setUp() {
        // Arrange
        firstHandler = Mockito.mock(HttpRequestChainElement.class);
        secondHandler = Mockito.mock(HttpRequestChainElement.class);
        thirdHandler = Mockito.mock(HttpRequestChainElement.class);

        HttpRequest mockHttpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(mockHttpRequest.requestStatusLine()).thenReturn(new RequestStatusLine(HttpMethod.GET, URI.create("http://localhost:8080"), new HttpVersion(1, 1)));
        request = Message.builder(new byte[0])
                .withDetails(new HttpRequestMessageDetails(mockHttpRequest))
                .build();
    }

    @Test
    void shouldExecuteHandlersInOrder() {
        // Arrange
        HttpRequestChain chain = new HttpRequestChain();
        HttpRequestChainElement first = Mockito.spy(new OrderedHandler(1));
        HttpRequestChainElement second = Mockito.spy(new OrderedHandler(2));
        HttpRequestChainElement third = Mockito.spy(new OrderedHandler(3));

        chain.addHandler(third);
        chain.addHandler(second);
        chain.addHandler(first);

        // Act
        chain.handle(request);

        // Assert
        Mockito.verify(first).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
        Mockito.verify(second).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
        Mockito.verify(third).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
    }

    @Test
    void shouldThrowExceptionWhenNoHandlersConfigured() {
        // Arrange
        HttpRequestChain chain = new HttpRequestChain();

        // Act Assert
        assertThrows(IllegalStateException.class, () -> chain.handle(request));
    }

    @Test
    void shouldStopChainWhenHandlerReturnsResponse() {
        // Arrange
        HttpRequestChain chain = new HttpRequestChain();
        chain.addHandler(firstHandler);
        chain.addHandler(secondHandler);
        chain.addHandler(thirdHandler);

        Mockito.when(firstHandler.getOrder()).thenReturn(1);
        Mockito.when(secondHandler.getOrder()).thenReturn(2);
        Mockito.when(thirdHandler.getOrder()).thenReturn(3);

        Message<HttpResponseMessageDetails> response = createMockResponse();
        Mockito.when(firstHandler.handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class)))
                .thenAnswer(invocation -> invocation.getArgument(1, HttpRequestChainContext.class).next(invocation.getArgument(0, Message.class)));
        Mockito.when(secondHandler.handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class)))
                .thenReturn(response);

        // Act
        Message<HttpResponseMessageDetails> result = chain.handle(request);

        // Assert
        assertSame(response, result);
        Mockito.verify(firstHandler).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
        Mockito.verify(secondHandler).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
        Mockito.verify(thirdHandler, Mockito.never()).handle(Mockito.eq(request), Mockito.any(HttpRequestChainContext.class));
    }

    @Test
    void shouldExecuteChain() {
        // Arrange
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.when(request.newResponse()).thenReturn(Mockito.mock(HttpResponse.class));
        Mockito.when(request.requestStatusLine()).thenReturn(new RequestStatusLine(HttpMethod.GET, URI.create("http://localhost:8080"), new HttpVersion(1, 1)));
        Message<HttpRequestMessageDetails> response = Message.builder(new byte[0])
                .withDetails(new HttpRequestMessageDetails(request))
                .build();

        TestHandler handler3 = new TestHandler(3);
        TestHandler handler1 = new TestHandler(1);
        TestHandler handler2 = new TestHandler(2);
        HttpRequestChain chain = new HttpRequestChain(List.of(handler1, handler2, handler3));

        // Act
        Message<HttpResponseMessageDetails> handle = chain.handle(response);

        // Assert
        MessageHeader invocations = handle.header("invocations");
        assertNotNull(invocations);
        assertEquals(3, invocations.decodeToInt());
        assertTrue(handler3.isLastInvocation());
    }

    private Message<HttpResponseMessageDetails> createMockResponse() {
        HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
        return Message.builder(new byte[0])
                .withDetails(new HttpResponseMessageDetails(request.details().request(), mockResponse))
                .build();
    }

    private record OrderedHandler(int order) implements HttpRequestChainElement {

        @Override
        public Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request, HttpRequestChainContext chain) {
            if (chain.hasNext()) {
                return chain.next(request);
            } else {
                return Mockito.mock(Message.class);
            }
        }

        @Override
        public int getOrder() {
            return order;
        }
    }

    private static class TestHandler implements HttpRequestChainElement {

        private final int order;
        private boolean lastInvocation = false;

        private TestHandler(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }

        @Override
        public Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request, HttpRequestChainContext chain) {
            int invocations = Optional.ofNullable(request.header("invocations")).map(i -> i.decodeToInt() + 1).orElse(1);

            if (chain.hasNext()) {
                return chain.next(
                        Message.builder(request.body())
                                .addHeader("invocations", Integer.toString(invocations))
                                .withDetails(request.details())
                                .build()

                );
            } else {
                lastInvocation = true;
                return Message.builder(new byte[0])
                        .addHeader("invocations", Integer.toString(invocations))
                        .withDetails(new HttpResponseMessageDetails(request.details().request(), request.details().request().newResponse()))
                        .build();
            }
        }

        public boolean isLastInvocation() {
            return lastInvocation;
        }
    }
}