package com.wiredi.web.api;

import com.wiredi.web.domain.HttpMethods;
import com.wiredi.web.domain.MediaTypes;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface RestRequestHandler {

    HttpMethods method();

    String path() default "";

    MediaTypes produces() default MediaTypes.APPLICATION_JSON;

}
