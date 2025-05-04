package com.wiredi.web.domain.converters;


import com.wiredi.runtime.types.AbstractTypeConverter;
import com.wiredi.runtime.types.Bytes;
import com.wiredi.web.domain.MediaType;

public class MediaTypePropertyConverter extends AbstractTypeConverter<MediaType> {

    public static final MediaTypePropertyConverter INSTANCE = new MediaTypePropertyConverter();

    public MediaTypePropertyConverter() {
        super(MediaType.class);
    }

    @Override
    protected void setup() {
        register(String.class, MediaType::resolve);
        register(byte[].class, b -> MediaType.resolve(Bytes.toString(b)));
    }
}
