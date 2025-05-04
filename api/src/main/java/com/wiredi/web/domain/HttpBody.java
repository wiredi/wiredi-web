package com.wiredi.web.domain;

import com.wiredi.web.utils.Arrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class HttpBody {

    private static final byte[] EMPTY_CONTENT = new byte[0];
    private byte[] content;

    public HttpBody() {
        this(EMPTY_CONTENT);
    }

    public HttpBody(byte[] content) {
        this.content = content;
    }

    public HttpBody(String content) {
        this(content.getBytes());
    }

    public static HttpBody empty() {
        return new HttpBody();
    }

    public static HttpBody of(byte[] content) {
        return new HttpBody(content);
    }

    public static HttpBody of(InputStream content) {
        try {
            return new HttpBody(content.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static HttpBody of(String content) {
        return new HttpBody(content);
    }

    public static HttpBody of(StringBuilder content) {
        return new HttpBody(content.toString());
    }

    public byte[] set(byte[] content) {
        byte[] original = this.content;
        this.content = content;
        return original;
    }

    public byte[] set(String content) {
        return this.set(content.getBytes());
    }

    public byte[] append(byte[] appendix) {
        byte[] original = this.content;
        this.content = Arrays.append(this.content, appendix);
        return original;
    }

    public byte[] prepend(byte[] prefix) {
        byte[] original = this.content;
        this.content = Arrays.prepend(this.content, prefix);
        return original;
    }

    public byte[] clear() {
        if (this.content == EMPTY_CONTENT) {
            return EMPTY_CONTENT;
        }

        byte[] original = this.content;
        this.content = EMPTY_CONTENT;
        return original;
    }

    public byte[] read() {
        return this.content;
    }
}
