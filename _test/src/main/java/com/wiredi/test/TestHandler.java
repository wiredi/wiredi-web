package com.wiredi.test;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.resources.builtin.ClassPathResource;
import com.wiredi.web.api.RestRequestHandler;
import com.wiredi.web.domain.HttpMethods;

@Wire
public class TestHandler {

    @RestRequestHandler(
            method = HttpMethods.GET,
            path = "/test"
    )
    public ClassPathResource testDocument() {
        return new ClassPathResource("resources/test");
    }
}
