package com.wiredi.web.request.errors;

import com.wiredi.logging.Logging;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.domain.HttpStatus;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Default500ExceptionHandler implements DefaultExceptionHandler {

    private static final Logging logger = Logging.getInstance(DefaultExceptionHandler.class);

    @Override
    public @NotNull ResponseEntity handle(@NotNull HttpRequest httpRequest, @NotNull HttpResponse httpResponse, @NotNull Throwable throwable) {
        logger.error("Unexpected error while handling request", throwable);
        httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return ResponseEntity.of(HttpStatus.INTERNAL_SERVER_ERROR)
                .withBody(new InternalServerErrorDetails(
                        "500 Internal Server Error",
                        httpRequest.path(),
                        throwable.getMessage(),
                        stringWriter.toString()
                ));
    }

    public record InternalServerErrorDetails(
            String error,
            String path,
            String message,
            String stacktrace
    ) {
    }
}
