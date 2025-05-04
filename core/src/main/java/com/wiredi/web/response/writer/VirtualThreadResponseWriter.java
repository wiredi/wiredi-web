package com.wiredi.web.response.writer;

import com.wiredi.web.response.ResponseWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadResponseWriter implements ResponseWriter {

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void execute(Runnable runnable) {
        executorService.submit(runnable);
    }
}
