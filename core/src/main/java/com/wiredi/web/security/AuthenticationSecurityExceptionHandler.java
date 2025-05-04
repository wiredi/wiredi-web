package com.wiredi.web.security;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.security.exceptions.UnauthenticatedException;
import com.wiredi.runtime.security.exceptions.UnauthorizedException;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.domain.HttpStatus;
import com.wiredi.web.request.errors.RequestExceptionHandler;
import com.wiredi.web.response.ResponseEntity;
import org.jetbrains.annotations.Nullable;

@Wire
public class AuthenticationSecurityExceptionHandler implements RequestExceptionHandler {

    @Override
    public boolean handles(Throwable throwable) {
        return throwable instanceof UnauthenticatedException || throwable instanceof UnauthorizedException;
    }

    @Override
    public @Nullable ResponseEntity handle(HttpRequest httpRequest, HttpResponse httpResponse, Throwable throwable) {
        if (throwable instanceof UnauthenticatedException) {
            return ResponseEntity.of(HttpStatus.UNAUTHORIZED);
        }
        if (throwable instanceof UnauthorizedException) {
            return ResponseEntity.of(HttpStatus.FORBIDDEN);
        }
        return null;
    }
}
