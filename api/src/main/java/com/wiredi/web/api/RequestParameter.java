package com.wiredi.web.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestParameter {

    String name() default "";

}
