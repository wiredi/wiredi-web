package com.wiredi.web;

import java.net.URI;

public class Paths {

    public static String resolvePath(URI uri) {
        String path = uri.getPath();

        if (path.startsWith("/")) {
            return path;
        } else {
            return "/" + path;
        }
    }

    public static String resolvePath(URI uri, String contextPath) {
        if (contextPath == null || contextPath.isBlank()) {
            return resolvePath(uri);
        }

        String path = uri.getPath();
        String cleaned = path.replaceFirst(contextPath, "");
        return startingWithSingleSlash(cleaned);
    }

    public static String startingWithSingleSlash(String entry) {
        String result = entry;
        if (result.startsWith("/")) {
            while (result.charAt(1) == '/') {
                result = result.substring(1);
            }

            return result;
        } else {
            return "/" + result;
        }
    }

    public static String endingWithSingleSlash(String entry) {
        String result = entry;
        if (result.endsWith("/")) {
            while (result.charAt(result.length() - 2) == '/') {
                result = result.substring(1);
            }

            return result;
        } else {
            return result + "/";
        }
    }
}
