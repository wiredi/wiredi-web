package com.wiredi.web;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A fluent builder for constructing URIs with a DSL-like syntax.
 * <p>
 * This builder provides methods for setting all components of a URI including scheme, host, port,
 * path, query parameters, and fragment. It also supports path variables for creating templates.
 * <p>
 * The builder uses method chaining to provide a fluent API for constructing URIs:
 * 
 * <pre>{@code
 * URI uri = UriBuilder.https()
 *     .host("api.example.com")
 *     .path("v1/users/{userId}")
 *     .pathVariable("userId", "123")
 *     .queryParameter("fields", "name,email")
 *     .build();
 * // Result: https://api.example.com/v1/users/123?fields=name%2Cemail
 * }</pre>
 * 
 * @see URI
 */
public class UriBuilder {
    private final List<String> pathSegments = new ArrayList<>();
    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private final Map<String, String> pathVariables = new HashMap<>();
    private String scheme;
    private String host;
    private Integer port;
    private String fragment;

    private UriBuilder() {}

    /**
     * Creates a new empty URI builder instance.
     * <p>
     * Example:
     * <pre>{@code
     * UriBuilder builder = UriBuilder.prepare()
     *     .scheme("https")
     *     .host("example.com");
     * }</pre>
     *
     * @return a new UriBuilder instance
     */
    public static UriBuilder prepare() {
        return new UriBuilder();
    }

    /**
     * Creates a URI builder initialized with the components of the given base URL.
     * <p>
     * This method parses the provided URL and sets the scheme, host, port, path,
     * query parameters, and fragment accordingly.
     * <p>
     * Example:
     * <pre>{@code
     * UriBuilder builder = UriBuilder.of("https://example.com:8443/api/v1?version=2");
     * // Initialized with scheme=https, host=example.com, port=8443, 
     * // path segments=[api, v1], query parameters={version=2}
     * }</pre>
     *
     * @param baseUrl a complete URL string to parse for initialization
     * @return a new UriBuilder initialized with the components of the baseUrl
     * @see #baseUrl(String)
     */
    public static UriBuilder of(String baseUrl) {
        return prepare().baseUrl(baseUrl);
    }

    /**
     * Creates a URI builder with the HTTP scheme preset.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.http()
     *     .host("example.com")
     *     .path("api")
     *     .build();
     * // Result: http://example.com/api
     * }</pre>
     *
     * @return a new UriBuilder with scheme set to "http"
     */
    public static UriBuilder http() {
        return prepare().scheme("http");
    }

    /**
     * Creates a URI builder with the HTTPS scheme preset.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("api")
     *     .build();
     * // Result: https://example.com/api
     * }</pre>
     *
     * @return a new UriBuilder with scheme set to "https"
     */
    public static UriBuilder https() {
        return prepare().scheme("https");
    }

    /**
     * Sets the URI scheme (e.g., "http", "https", "ftp").
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.prepare()
     *     .scheme("https")
     *     .host("example.com")
     *     .build();
     * // Result: https://example.com/
     * }</pre>
     *
     * @param scheme the scheme part of the URI
     * @return this builder instance for method chaining
     */
    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Sets the URI scheme (e.g., "http", "https", "ftp").
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.prepare()
     *     .scheme(UriScheme.HTTPS)
     *     .host("example.com")
     *     .build();
     * // Result: https://example.com/
     * }</pre>
     *
     * @param scheme the scheme part of the URI
     * @return this builder instance for method chaining
     */
    public UriBuilder scheme(UriScheme scheme) {
        this.scheme = scheme.getValue();
        return this;
    }

    /**
     * Sets the host component of the URI.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("api.example.com")
     *     .build();
     * // Result: https://api.example.com/
     * }</pre>
     *
     * @param host the host name or IP address
     * @return this builder instance for method chaining
     */
    public UriBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the port number for the URI.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .port(8443)
     *     .segment("foo")
     *     .build();
     * // Result: https://example.com:8443/foo
     * }</pre>
     *
     * @param port the port number
     * @return this builder instance for method chaining
     */
    public UriBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Adds a path to the URI, automatically handling multiple segments.
     * <p>
     * This method splits the provided path by "/" and adds each non-empty segment to the path.
     * Multiple forward slashes are handled properly and empty segments are ignored.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("api/v1/users")
     *     .build();
     * // Result: https://example.com/api/v1/users
     * 
     * // Equivalent to:
     * URI uri2 = UriBuilder.https()
     *     .host("example.com")
     *     .segment("api")
     *     .segment("v1")
     *     .segment("users")
     *     .build();
     * }</pre>
     *
     * @param path a path string that may contain multiple segments separated by "/"
     * @return this builder instance for method chaining
     * @see #segment(String)
     */
    public UriBuilder path(String path) {
        if (path == null || path.isEmpty()) return this;
        String[] segments = path.split("/");
        for (String segment : segments) {
            if (!segment.isEmpty()) {
                pathSegments.add(segment);
            }
        }
        return this;
    }

    /**
     * Adds a single path segment to the URI.
     * <p>
     * Unlike {@link #path(String)}, this method does not split the input by "/",
     * but adds it as a single segment.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .segment("api")
     *     .segment("v1")
     *     .segment("users")
     *     .build();
     * // Result: https://example.com/api/v1/users
     * }</pre>
     *
     * @param segment a single path segment to add
     * @return this builder instance for method chaining
     * @see #path(String)
     */
    public UriBuilder segment(String segment) {
        if (segment != null && !segment.isEmpty()) {
            pathSegments.add(segment);
        }
        return this;
    }

    /**
     * Adds a query parameter to the URI.
     * <p>
     * The parameter name and value will be properly URL-encoded in the final URI.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("search")
     *     .queryParameter("q", "java programming")
     *     .queryParameter("page", "1")
     *     .build();
     * // Result: https://example.com/search?q=java+programming&page=1
     * }</pre>
     *
     * @param name the name of the query parameter
     * @param value the value of the query parameter
     * @return this builder instance for method chaining
     */
    public UriBuilder queryParameter(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    /**
     * Adds multiple query parameters to the URI at once.
     * <p>
     * All parameter names and values will be properly URL-encoded in the final URI.
     * <p>
     * Example:
     * <pre>{@code
     * Map<String, String> params = new HashMap<>();
     * params.put("q", "java");
     * params.put("page", "1");
     * params.put("sort", "desc");
     * 
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("search")
     *     .queryParameters(params)
     *     .build();
     * // Result: https://example.com/search?q=java&page=1&sort=desc
     * }</pre>
     *
     * @param params a map of query parameter names to values
     * @return this builder instance for method chaining
     */
    public UriBuilder queryParameters(Map<String, String> params) {
        queryParams.putAll(params);
        return this;
    }

    /**
     * Sets a path variable that will be substituted in the path.
     * <p>
     * Path variables are placeholders in the form of {name} that will be replaced
     * with the provided value during URI construction. This allows for creating
     * URI templates.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("users/{userId}/posts/{postId}")
     *     .pathVariable("userId", "123")
     *     .pathVariable("postId", "456")
     *     .build();
     * // Result: https://example.com/users/123/posts/456
     * }</pre>
     *
     * @param name the name of the path variable (with or without curly braces)
     * @param value the value to substitute for the path variable
     * @return this builder instance for method chaining
     */
    public UriBuilder pathVariable(String name, String value) {
        pathVariables.put(formatVar(name), value);
        return this;
    }

    /**
     * Sets the fragment component of the URI (the part after '#').
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("example.com")
     *     .path("docs/api")
     *     .fragment("section-3.2")
     *     .build();
     * // Result: https://example.com/docs/api#section-3.2
     * }</pre>
     *
     * @param fragment the fragment identifier
     * @return this builder instance for method chaining
     */
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Parses a complete base URL and configures the builder with its components.
     * <p>
     * This method extracts the scheme, host, port, path, query parameters, and fragment
     * from the provided URL and sets them on this builder. If the URL is invalid,
     * it will be treated as a simple path.
     * <p>
     * Example:
     * <pre>{@code
     * UriBuilder builder = UriBuilder.prepare()
     *     .baseUrl("https://api.example.com:8443/v1/search?q=test#results")
     *     .segment("additional")
     *     .build();
     * // Result: https://api.example.com:8443/v1/search/additional?q=test#results
     * }</pre>
     * 
     * @param baseUrl A complete URL string (e.g., "https://example.com:8080/api/v1")
     * @return this builder instance for method chaining
     */
    public UriBuilder baseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return this;
        }
        
        try {
            URI uri = URI.create(baseUrl);
            
            // Set scheme, host, port
            if (uri.getScheme() != null) {
                this.scheme = uri.getScheme();
            }
            
            if (uri.getHost() != null) {
                this.host = uri.getHost();
            }
            
            if (uri.getPort() != -1) {
                this.port = uri.getPort();
            }
            
            // Set path
            if (uri.getPath() != null && !uri.getPath().isEmpty()) {
                path(uri.getPath());
            }
            
            // Set query parameters if present
            if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
                String[] queryParts = uri.getQuery().split("&");
                for (String part : queryParts) {
                    String[] keyValue = part.split("=", 2);
                    if (keyValue.length == 2) {
                        queryParameter(keyValue[0], keyValue[1]);
                    }
                }
            }
            
            // Set fragment if present
            if (uri.getFragment() != null) {
                this.fragment = uri.getFragment();
            }
            
        } catch (IllegalArgumentException e) {
            // If the URL is not properly formatted, just add it as a path
            path(baseUrl);
        }
        
        return this;
    }
    
    /**
     * Formats a variable name to ensure it has the required curly braces format.
     * 
     * @param name the variable name, with or without curly braces
     * @return the variable name with curly braces added if needed
     */
    private String formatVar(String name) {
        if (!name.startsWith("{")) name = "{" + name;
        if (!name.endsWith("}")) name = name + "}";
        return name;
    }

    /**
     * Builds the URI based on the current state of this builder.
     * <p>
     * This method constructs a URI by combining all components that have been set
     * on this builder, including scheme, host, port, path segments, path variables,
     * query parameters, and fragment.
     * <p>
     * Example:
     * <pre>{@code
     * URI uri = UriBuilder.https()
     *     .host("api.example.com")
     *     .port(443)
     *     .path("v1/users/{userId}")
     *     .pathVariable("userId", "123")
     *     .queryParameter("fields", "name,email")
     *     .fragment("personal-info")
     *     .build();
     * }</pre>
     *
     * @return a new URI instance built from the components set on this builder
     * @throws IllegalArgumentException if the resulting URI string is not a valid URI
     */
    public URI build() {
        StringBuilder uriBuilder = new StringBuilder();

        // Scheme and authority
        if (scheme != null) {
            uriBuilder.append(scheme).append("://");
        }

        if (host != null) {
            uriBuilder.append(host);
            if (port != null) {
                uriBuilder.append(":").append(port);
            }
        }

        // Path
        String path = buildPath();
        if (!path.startsWith("/")) {
            uriBuilder.append("/");
        }
        uriBuilder.append(path);

        // Query parameters
        if (!queryParams.isEmpty()) {
            uriBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (!first) {
                    uriBuilder.append("&");
                }
                uriBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                first = false;
            }
        }

        // Fragment
        if (fragment != null) {
            uriBuilder.append("#").append(fragment);
        }

        return URI.create(uriBuilder.toString());
    }

    /**
     * Builds the path component by joining path segments and substituting path variables.
     * 
     * @return the complete path string with variables substituted
     */
    private String buildPath() {
        if (pathSegments.isEmpty()) return "";

        String path = String.join("/", pathSegments);

        // Replace path variables
        for (Map.Entry<String, String> var : pathVariables.entrySet()) {
            path = path.replace(var.getKey(), var.getValue());
        }

        return path;
    }

    /**
     * Builds the URI and returns it as a string.
     * <p>
     * This is a convenience method equivalent to calling {@code build().toString()}.
     * <p>
     * Example:
     * <pre>{@code
     * String uriString = UriBuilder.https()
     *     .host("example.com")
     *     .path("api")
     *     .buildString();
     * // Result: "https://example.com/api"
     * }</pre>
     *
     * @return a string representation of the URI
     * @see #build()
     */
    public String buildString() {
        return build().toString();
    }
}