package com.wiredi.web.exceptions;

import com.wiredi.web.domain.HttpStatus;

public class NotFoundException extends HttpStatusCodeException {
    public NotFoundException(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
