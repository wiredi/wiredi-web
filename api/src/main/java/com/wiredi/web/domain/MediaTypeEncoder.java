package com.wiredi.web.domain;

import java.util.List;

public interface MediaTypeEncoder {

    byte[] encode(Object object, String mediaType);

    List<String> supportedMediaTypes();

}
