package com.wiredi.web.messaging;

import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;

public record HttpResponseMessageDetails(HttpRequest request, HttpResponse response) implements HttpMessageDetails {
}
