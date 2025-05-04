package com.wiredi.web.domain;

import com.wiredi.web.exceptions.HttpFormatException;

public record HttpVersion(int major, int minor) {

    @Override
    public String toString() {
        return "HTTP/" + major + "." + minor;
    }

    public static HttpVersion parse(String raw) {
        String[] rawHttpVersion = raw.replace("HTTP/", "").split("\\.");
        if (rawHttpVersion.length != 2) {
            throw new HttpFormatException("Invalid http version. Expected \"HTTP/<MAJOR>.<MINOR>\", but got " + raw);
        }

        try {
            return new HttpVersion(Integer.parseInt(rawHttpVersion[0]), Integer.parseInt(rawHttpVersion[1]));
        } catch (NumberFormatException e) {
            throw new HttpFormatException("Invalid http version numbers. Unable to parse Integer from http version: " + raw, e);
        }
    }
}
