package com.wiredi.web.request;

import com.wiredi.annotations.Wire;
import com.wiredi.metrics.MetricAware;
import com.wiredi.metrics.MetricCollector;
import com.wiredi.runtime.lang.OrderedComparator;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.messaging.HttpRequestMessageDetails;
import com.wiredi.web.messaging.HttpResponseMessageDetails;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Wire(proxy = false)
public class HttpRequestChain implements HttpRequestHandler, MetricAware {

    private final List<HttpRequestChainElement> handlers = new ArrayList<>();
    private final ReentrantLock sortLock = new ReentrantLock();
    private MetricCollector metricCollector = MetricCollector.noop();
    private boolean sorted = true;

    @Override
    public void setMetricCollector(MetricCollector collector) {
        metricCollector = collector;
    }

    @Inject
    public HttpRequestChain(List<HttpRequestChainElement> handlers) {
        this.handlers.addAll(OrderedComparator.sorted(handlers));
    }

    public HttpRequestChain() {
        this(List.of());
    }

    public void addHandler(HttpRequestChainElement handler) {
        handlers.add(handler);
        sorted = false;
    }

    public void sortHandlers() {
        try {
            sortLock.lock();
            if (sorted) {
                return;
            }
            OrderedComparator.sort(handlers);
            sorted = true;
        } finally {
            sortLock.unlock();
        }
    }

    @Override
    public Message<HttpResponseMessageDetails> handle(Message<HttpRequestMessageDetails> request) {
        if (!sorted) {
            sortHandlers();
        }

        if (handlers.isEmpty()) {
            throw new IllegalStateException("No handlers configured in the chain");
        }

        ChainContext context = new ChainContext(new ArrayList<>(handlers).iterator());
        return metricCollector.timer("webserver.request-chain.execution-time").record(() -> context.next(request));
    }

    private record ChainContext(Iterator<HttpRequestChainElement> handlersIterator) implements HttpRequestChainContext {

        @Override
        public boolean hasNext() {
            return handlersIterator.hasNext();
        }

        @Override
        public Message<HttpResponseMessageDetails> next(Message<HttpRequestMessageDetails> request) {
            if (handlersIterator.hasNext()) {
                HttpRequestChainElement handler = handlersIterator.next();
                return handler.handle(request, this);
            }
            return null;
        }
    }
}