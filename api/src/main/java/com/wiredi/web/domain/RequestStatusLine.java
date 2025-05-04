package com.wiredi.web.domain;

import com.google.common.base.Strings;
import com.wiredi.web.exceptions.HttpFormatException;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.AbstractMap.SimpleImmutableEntry;

/**
 * https://datatracker.ietf.org/doc/html/rfc2616#section-5.1
 */
public record RequestStatusLine(
        HttpMethod httpMethod,
        URI uri,
        HttpVersion httpVersion
) {
    // Method SP Request-URI SP HTTP-Version CRLF
    public static RequestStatusLine parse(String raw) throws HttpFormatException {
        String[] split = raw.split("\\s");
        if (split.length != 3) {
            throw new HttpFormatException("Invalid request line: expected 3 entries, but got " + Arrays.toString(split));
        }

        HttpMethod httpMethod = new HttpMethod(split[0]);
        URI uri = URI.create(split[1]);
        HttpVersion httpVersion = HttpVersion.parse(split[2]);

        return new RequestStatusLine(httpMethod, uri, httpVersion);
    }

    public Map<String, List<String>> requestParameters() {
        if (Strings.isNullOrEmpty(uri.getQuery())) {
            return Collections.emptyMap();
        }
        return Arrays.stream(uri.getQuery().split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    public SimpleImmutableEntry<String, String> splitQueryParameter(String queryEntry) {
        final int idx = queryEntry.indexOf("=");
        final String key = idx > 0 ? queryEntry.substring(0, idx) : queryEntry;
        final String value = idx > 0 && queryEntry.length() > idx + 1 ? queryEntry.substring(idx + 1) : null;
        return new SimpleImmutableEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                Optional.ofNullable(value).map(it -> URLDecoder.decode(it, StandardCharsets.UTF_8)).orElse(null)
        );
    }

}
