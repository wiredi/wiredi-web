package com.wiredi.web.domain;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SimpleHttpStatusCode implements HttpStatusCode, Comparable<SimpleHttpStatusCode>, Serializable {

    private final int value;
    private final String reasonPhrase;

    public SimpleHttpStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return value;
    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public boolean is1xxInformational() {
        return hundreds() == 1;
    }

    @Override
    public boolean is2xxSuccessful() {
        return hundreds() == 2;
    }

    @Override
    public boolean is3xxRedirection() {
        return hundreds() == 3;
    }

    @Override
    public boolean is4xxClientError() {
        return hundreds() == 4;
    }

    @Override
    public boolean is5xxServerError() {
        return hundreds() == 5;
    }

    @Override
    public boolean isError() {
        int hundreds = hundreds();
        return hundreds == 4 || hundreds == 5;
    }

    private int hundreds() {
        return this.value / 100;
    }

    @Override
    public int compareTo(@NotNull SimpleHttpStatusCode o) {
        return Integer.compare(this.value, o.value());
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SimpleHttpStatusCode other && this.value == other.value());
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
