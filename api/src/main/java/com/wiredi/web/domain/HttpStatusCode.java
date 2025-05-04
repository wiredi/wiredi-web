package com.wiredi.web.domain;

public interface HttpStatusCode {

    static HttpStatusCode resolve(int statusCode) {
        return HttpStatus.valueOf(statusCode);
    }

    int value();

    String getReasonPhrase();

    boolean is1xxInformational();

    boolean is2xxSuccessful();

    boolean is3xxRedirection();

    boolean is4xxClientError();

    boolean is5xxServerError();

    boolean isError();
}
