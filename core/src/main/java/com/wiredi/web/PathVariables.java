package com.wiredi.web;

import com.wiredi.runtime.values.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PathVariables {

    private final String pattern;
    private final String path;
    private final Value<Map<String, String>> values;
    private static final AntPathMatcher matcher = new AntPathMatcher();

    public PathVariables(String pattern, String path) {
        this.pattern = pattern;
        this.path = path;
        values = Value.lazy(() -> matcher.extractUriTemplateVariables(pattern, path));
    }

    @Nullable
    public String get(@NotNull String name) {
        return values.get().get(name);
    }
}
