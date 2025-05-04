package com.wiredi.web.request.errors;

import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.Nullable;

public interface RequestExceptionHandler {

    boolean handles(Throwable throwable);

    @Nullable
    ResponseEntity handle(HttpRequest httpRequest, HttpResponse httpResponse, Throwable throwable);

}
