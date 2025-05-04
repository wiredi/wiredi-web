package com.wiredi.web;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UriBuilderTest {

    @Test
    void testStaticFactories() {
        // Test create()
        assertNotNull(UriBuilder.prepare());
        
        // Test http()
        assertEquals("http", UriBuilder.http().build().getScheme());
        
        // Test https()
        assertEquals("https", UriBuilder.https().build().getScheme());
        
        // Test from()
        URI builder = UriBuilder.of("https://example.com").build();
        assertEquals("https", builder.getScheme());
        assertEquals("example.com", builder.getHost());
    }

    @Test
    void testSchemeHostPort() {
        URI uri = UriBuilder.prepare()
                .scheme("https")
                .host("example.com")
                .port(8443)
                .build();
        
        assertEquals("https://example.com:8443/", uri.toString());
    }
    
    @Test
    void testPathBuilding() {
        // Test single path segment
        URI uri1 = UriBuilder.prepare()
                .host("example.com")
                .path("api")
                .build();
        assertEquals("example.com/api", uri1.toString());
        
        // Test multiple path segments with path()
        URI uri2 = UriBuilder.prepare()
                .host("example.com")
                .path("api/v1/users")
                .build();
        assertEquals("example.com/api/v1/users", uri2.toString());
        
        // Test individual segments with segment()
        URI uri3 = UriBuilder.prepare()
                .host("example.com")
                .segment("api")
                .segment("v1")
                .segment("users")
                .build();
        assertEquals("example.com/api/v1/users", uri3.toString());
        
        // Test empty path segments are ignored
        URI uri4 = UriBuilder.prepare()
                .host("example.com")
                .path("api//v1///users")
                .build();
        assertEquals("example.com/api/v1/users", uri4.toString());
        
        // Test null and empty paths
        URI uri5 = UriBuilder.prepare()
                .host("example.com")
                .path(null)
                .path("")
                .build();
        assertEquals("example.com/", uri5.toString());
    }
    
    @Test
    void testQueryParameters() {
        // Test single parameter
        URI uri1 = UriBuilder.prepare()
                .host("example.com")
                .path("search")
                .queryParameter("q", "test")
                .build();
        assertEquals("example.com/search?q=test", uri1.toString());
        
        // Test multiple parameters
        URI uri2 = UriBuilder.prepare()
                .host("example.com")
                .path("search")
                .queryParameter("q", "test")
                .queryParameter("page", "1")
                .queryParameter("limit", "10")
                .build();
        assertEquals("example.com/search?q=test&page=1&limit=10", uri2.toString());
        
        // Test parameters with special characters
        URI uri3 = UriBuilder.prepare()
                .host("example.com")
                .path("search")
                .queryParameter("q", "test with spaces")
                .queryParameter("filter", "name=John&age=30")
                .build();
        assertTrue(uri3.toString().contains("q=test+with+spaces"));
        assertTrue(uri3.toString().contains("filter=name%3DJohn%26age%3D30"));
        
        // Test adding parameters from map
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");
        params.put("page", "1");
        
        URI uri4 = UriBuilder.prepare()
                .host("example.com")
                .path("search")
                .queryParameters(params)
                .build();
        assertTrue(uri4.toString().contains("q=test"));
        assertTrue(uri4.toString().contains("page=1"));
    }
    
    @Test
    void testPathVariables() {
        // Test simple path variable
        URI uri1 = UriBuilder.prepare()
                .host("example.com")
                .path("users/{userId}")
                .pathVariable("userId", "123")
                .build();
        assertEquals("example.com/users/123", uri1.toString());
        
        // Test multiple path variables
        URI uri2 = UriBuilder.prepare()
                .host("example.com")
                .path("users/{userId}/posts/{postId}")
                .pathVariable("userId", "123")
                .pathVariable("postId", "456")
                .build();
        assertEquals("example.com/users/123/posts/456", uri2.toString());
        
        // Test with or without braces in variable name
        URI uri3 = UriBuilder.prepare()
                .host("example.com")
                .path("users/{userId}/posts/{postId}")
                .pathVariable("{userId}", "123")
                .pathVariable("postId", "456")
                .build();
        assertEquals("example.com/users/123/posts/456", uri3.toString());
    }
    
    @Test
    void testFragments() {
        URI uri = UriBuilder.prepare()
                .host("example.com")
                .path("docs/api")
                .fragment("section-3.2")
                .build();
        
        assertEquals("example.com/docs/api#section-3.2", uri.toString());
    }
    
    @Test
    void testBaseUrlParsing() {
        // Test complete URL parsing
        URI uri1 = UriBuilder.of("https://example.com:8443/api/v1?q=test#section")
                .build();
        
        assertEquals("https", uri1.getScheme());
        assertEquals("example.com", uri1.getHost());
        assertEquals(8443, uri1.getPort());
        assertEquals("/api/v1", uri1.getPath());
        assertEquals("q=test", uri1.getQuery());
        assertEquals("section", uri1.getFragment());
        
        // Test URL without query or fragment
        URI uri2 = UriBuilder.of("https://example.com/api/v1")
                .build();
        
        assertEquals("https", uri2.getScheme());
        assertEquals("example.com", uri2.getHost());
        assertEquals("/api/v1", uri2.getPath());
        assertNull(uri2.getQuery());
        assertNull(uri2.getFragment());
        
        // Test adding additional components to parsed base URL
        URI uri3 = UriBuilder.of("https://example.com/api")
                .segment("v1")
                .segment("users")
                .queryParameter("active", "true")
                .build();
        
        assertEquals("https://example.com/api/v1/users?active=true", uri3.toString());
        
        // Test invalid URL fallback
        URI uri4 = UriBuilder.of("not-a-valid-url")
                .build();
        
        assertEquals("/not-a-valid-url", uri4.toString());
    }
    
    @Test
    void testBuildString() {
        String uri = UriBuilder.https()
                .host("example.com")
                .path("api/v1")
                .buildString();
        
        assertEquals("https://example.com/api/v1", uri);
    }
    
    @Test
    void testCompleteUri() {
        URI uri = UriBuilder.https()
                .host("api.example.com")
                .port(443)
                .path("v1/users/{userId}")
                .pathVariable("userId", "123")
                .queryParameter("include", "details")
                .queryParameter("fields", "name,email")
                .fragment("personal-info")
                .build();
        
        String expected = "https://api.example.com:443/v1/users/123?include=details&fields=name%2Cemail#personal-info";
        assertEquals(expected, uri.toString());
    }
    
    @Test
    void testEdgeCases() {
        // Test null inputs
        URI uri1 = UriBuilder.prepare()
                .host(null)
                .path(null)
                .segment(null)
                .build();
        assertEquals("/", uri1.toString());
        
        // Test empty base URL
        URI uri2 = UriBuilder.of("")
                .build();
        assertEquals("/", uri2.toString());
        
        // Test null base URL
        URI uri3 = UriBuilder.of(null)
                .build();
        assertEquals("/", uri3.toString());
    }
}
