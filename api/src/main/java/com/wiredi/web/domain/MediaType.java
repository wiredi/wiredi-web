package com.wiredi.web.domain;

import java.util.Objects;

public class MediaType {

    public static final String ALL_VALUE = "*/*";

    public static final MediaType ALL = new MediaType("*", "*");

    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

    public static final MediaType APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");

    public static final String APPLICATION_CBOR_VALUE = "application/cbor";

    public static final MediaType APPLICATION_CBOR = new MediaType("application", "cbor");

    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");

    public static final String APPLICATION_GRAPHQL_RESPONSE_VALUE = "application/graphql-response+json";

    public static final MediaType APPLICATION_GRAPHQL_RESPONSE = new MediaType("application", "graphql-response+json");

    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static final MediaType APPLICATION_JSON = new MediaType("application", "json");

    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    public static final MediaType APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");

    public static final String APPLICATION_PDF_VALUE = "application/pdf";

    public static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

    public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";

    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

    public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";

    public static final MediaType APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");

    public static final String APPLICATION_PROTOBUF_VALUE = "application/x-protobuf";

    public static final MediaType APPLICATION_PROTOBUF = new MediaType("application", "x-protobuf");

    public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";

    public static final MediaType APPLICATION_RSS_XML = new MediaType("application", "rss+xml");

    public static final String APPLICATION_NDJSON_VALUE = "application/x-ndjson";

    public static final MediaType APPLICATION_NDJSON = new MediaType("application", "x-ndjson");

    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

    public static final MediaType APPLICATION_XHTML_XML = new MediaType("application", "xhtml+xml");

    public static final String APPLICATION_XML_VALUE = "application/xml";

    public static final MediaType APPLICATION_XML = new MediaType("application", "xml");

    public static final String IMAGE_GIF_VALUE = "image/gif";

    public static final MediaType IMAGE_GIF = new MediaType("image", "gif");

    public static final String IMAGE_JPEG_VALUE = "image/jpeg";

    public static final MediaType IMAGE_JPEG = new MediaType("image", "jpeg");

    public static final String IMAGE_PNG_VALUE = "image/png";

    public static final MediaType IMAGE_PNG = new MediaType("image", "png");

    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    public static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");

    public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";

    public static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");

    public static final String MULTIPART_RELATED_VALUE = "multipart/related";

    public static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");

    public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";

    public static final MediaType TEXT_EVENT_STREAM = new MediaType("text", "event-stream");

    public static final String TEXT_HTML_VALUE = "text/html";

    public static final MediaType TEXT_HTML = new MediaType("text", "html");

    public static final String TEXT_MARKDOWN_VALUE = "text/markdown";

    public static final MediaType TEXT_MARKDOWN = new MediaType("text", "markdown");

    public static final String TEXT_PLAIN_VALUE = "text/plain";

    public static final MediaType TEXT_PLAIN = new MediaType("text", "plain");

    public static final String TEXT_XML_VALUE = "text/xml";

    public static final MediaType TEXT_XML = new MediaType("text", "xml");

    private final String type;
    private final String subtype;
    private final String compiled;

    public MediaType(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
        this.compiled = type + "/" + subtype;
    }

    public static MediaType resolve(String type) {
        if (!type.contains("/")) {
            throw new IllegalArgumentException("Invalid media type: " + type + ". No type/subtype found.");
        }
        String toSplit = type;
        if (type.contains(";")) {
            toSplit = type.split(";")[0];
        }
        String[] split = toSplit.split("/");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid media type: " + toSplit + ". Expected exactly one type/subtype separator.");
        }
        return new MediaType(split[0], split[1]);
    }

    @Override
    public String toString() {
        return compiled;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MediaType mediaType)) return false;
        return Objects.equals(type, mediaType.type) && Objects.equals(subtype, mediaType.subtype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subtype);
    }
}
