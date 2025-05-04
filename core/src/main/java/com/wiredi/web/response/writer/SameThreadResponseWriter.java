package com.wiredi.web.response.writer;

import com.wiredi.web.response.ResponseWriter;

public class SameThreadResponseWriter implements ResponseWriter {

    public static final ResponseWriter INSTANCE = new SameThreadResponseWriter();

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
