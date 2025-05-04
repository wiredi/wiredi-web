package com.wiredi.web.plugin;

import com.wiredi.compiler.logger.Logger;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.web.domain.HttpRequest;
import com.wiredi.web.domain.HttpResponse;
import com.wiredi.web.response.ResponseEntity;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class VariableContext {

    private static final Logger logger = Logger.get(VariableContext.class);
    private final Elements elements;
    private final Types types;
    private final TypeMirror httpRequestType;
    private final TypeMirror httpResponseType;
    private final TypeMirror headersType;
    private final TypeMirror responseEntityType;
    private final TypeMirror messageType;

    public VariableContext(Elements elements, Types types) {
        this.elements = elements;
        this.types = types;
        httpRequestType = this.elements.getTypeElement(HttpRequest.class.getName()).asType();
        httpResponseType = this.elements.getTypeElement(HttpResponse.class.getName()).asType();
        headersType = this.elements.getTypeElement(MessageHeaders.class.getName()).asType();
        responseEntityType = this.elements.getTypeElement(ResponseEntity.class.getName()).asType();
        messageType = this.elements.getTypeElement(Message.class.getName()).asType();
    }

    public boolean isOfType(Element element, TypeMirror type) {
        return isOfType(element.asType(), type);
    }

    public boolean isOfType(TypeMirror element, TypeMirror type) {
        return element == type || types.isAssignable(this.types.erasure(element), type);
    }

    public boolean isHttpRequest(Element element) {
        return isOfType(element, httpRequestType);
    }

    public boolean isHttpResponse(Element element) {
        return isOfType(element, httpResponseType);
    }

    public boolean isHeaders(Element element) {
        return isOfType(element, headersType);
    }

    public boolean isResponseEntity(Element element) {
        return isOfType(element, responseEntityType);
    }

    public boolean isResponseEntity(TypeMirror element) {
        return isOfType(element, responseEntityType);
    }

    public boolean isMessage(Element element) {
        return isOfType(element, messageType);
    }
}
