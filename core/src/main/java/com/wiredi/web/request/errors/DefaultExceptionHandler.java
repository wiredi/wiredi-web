package com.wiredi.web.request.errors;

import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

public interface DefaultExceptionHandler {

    @NotNull
    ResponseEntity handle(@NotNull HttpRequest httpRequest, @NotNull HttpResponse httpResponse, @NotNull Throwable throwable);
}
