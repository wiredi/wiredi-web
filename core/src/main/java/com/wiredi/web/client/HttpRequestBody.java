package com.wiredi.web.client;

import java.io.InputStream;

public sealed interface HttpRequestBody {

    record Raw(byte[] bytes) implements HttpRequestBody {
    }

    record Stream(InputStream inputStream) implements HttpRequestBody {
    }
}
