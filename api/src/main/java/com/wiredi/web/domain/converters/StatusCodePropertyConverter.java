package com.wiredi.web.domain.converters;

import com.wiredi.runtime.types.AbstractTypeConverter;
import com.wiredi.runtime.types.Bytes;
import com.wiredi.runtime.types.converter.IntTypeConverter;
import com.wiredi.web.domain.HttpStatusCode;

import java.util.Optional;

public class StatusCodePropertyConverter extends AbstractTypeConverter<HttpStatusCode> {

    public static final StatusCodePropertyConverter INSTANCE = new StatusCodePropertyConverter();

    public StatusCodePropertyConverter() {
        super(HttpStatusCode.class);
    }

    @Override
    protected void setup() {
        register(Integer.class, HttpStatusCode::resolve);
        register(String.class, this::parse);
        register(byte[].class, it -> parse(Bytes.toString(it)));
    }

    private HttpStatusCode parse(byte[] bytes) {
        return Optional.ofNullable(IntTypeConverter.INSTANCE.convert(bytes))
                .map(HttpStatusCode::resolve)
                .orElse(null);
    }

    private HttpStatusCode parse(String string) {
        return Optional.ofNullable(IntTypeConverter.INSTANCE.convert(string))
                .map(HttpStatusCode::resolve)
                .orElse(null);
    }
}
