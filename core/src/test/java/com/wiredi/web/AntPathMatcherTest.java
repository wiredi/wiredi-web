package com.wiredi.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AntPathMatcherTest {

    private AntPathMatcher antPathMatcher;

    public static List<Arguments> shouldMatch() {
        String path = "/api/test/groups/12345/users/1";

        return List.of(
                Arguments.arguments("/api/test/groups/{groupId}/users/{userId}", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/{userId}", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/{userId}", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/{userId}", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/?", path),
                Arguments.arguments("/api/test/groups/?????/users/?", path),

                Arguments.arguments("/???/test/groups/12345/users/1", path),
                Arguments.arguments("/api/????/groups/12345/users/1", path),
                Arguments.arguments("/api/test/??????/12345/users/1", path),
                Arguments.arguments("/api/test/groups/?????/users/1", path),
                Arguments.arguments("/api/test/groups/12345/?????/1", path),
                Arguments.arguments("/api/test/groups/12345/users/?", path),

                Arguments.arguments("/???/????/groups/12345/users/1", path),
                Arguments.arguments("/???/test/??????/12345/users/1", path),
                Arguments.arguments("/???/test/groups/?????/users/1", path),
                Arguments.arguments("/???/test/groups/12345/?????/1", path),
                Arguments.arguments("/???/test/groups/12345/users/?", path),

                Arguments.arguments("/???/????/??????/12345/users/1", path),
                Arguments.arguments("/???/????/groups/?????/users/1", path),
                Arguments.arguments("/???/????/groups/12345/?????/1", path),
                Arguments.arguments("/???/????/groups/12345/users/?", path),

                Arguments.arguments("/???/????/??????/?????/users/1", path),
                Arguments.arguments("/???/????/??????/12345/?????/1", path),
                Arguments.arguments("/???/????/??????/12345/users/?", path),

                Arguments.arguments("/???/????/??????/?????/?????/1", path),
                Arguments.arguments("/???/????/??????/?????/users/?", path),

                Arguments.arguments("/???/????/??????/?????/?????/?", path),

                Arguments.arguments("/api/test/groups/?????/*/?", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/**", path),
                Arguments.arguments("/api/test/groups/{groupId}/**", path),
                Arguments.arguments("/api/test/groups/**", path),
                Arguments.arguments("/api/test/**", path),
                Arguments.arguments("/api/**", path),
                Arguments.arguments("/**", path),
                Arguments.arguments("/**/groups/**/users/**", path),
                Arguments.arguments("/**/users/**", path)
        );
    }

    public static List<Arguments> shouldNotMatch() {
        String path = "/api/test/groups/12345/users/12345";

        return List.of(
                Arguments.arguments("/api/groups/test/{groupId}/users/{userId}", path),
                Arguments.arguments("/api/test/groups/?/users/{userId}", path),
                Arguments.arguments("/api/test/groups/??/users/{userId}", path),
                Arguments.arguments("/api/test/groups/???/users/{userId}", path),
                Arguments.arguments("/api/test/groups/????/users/{userId}", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/?", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/??", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/???", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/????", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/?", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/??", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/???", path),
                Arguments.arguments("/api/test/groups/{groupId}/users/????", path),
                Arguments.arguments("/api/test/groups/?/users/?", path),
                Arguments.arguments("/api/test/groups/?/users/??", path),
                Arguments.arguments("/api/test/groups/?/users/???", path),
                Arguments.arguments("/api/test/groups/?/users/????", path),
                Arguments.arguments("/api/test/groups/?/users/?????", path),
                Arguments.arguments("/api/test/groups/??/users/?", path),
                Arguments.arguments("/api/test/groups/??/users/??", path),
                Arguments.arguments("/api/test/groups/??/users/???", path),
                Arguments.arguments("/api/test/groups/??/users/????", path),
                Arguments.arguments("/api/test/groups/??/users/?????", path),
                Arguments.arguments("/api/test/groups/???/users/?", path),
                Arguments.arguments("/api/test/groups/???/users/??", path),
                Arguments.arguments("/api/test/groups/???/users/???", path),
                Arguments.arguments("/api/test/groups/???/users/????", path),
                Arguments.arguments("/api/test/groups/???/users/?????", path),
                Arguments.arguments("/api/test/groups/????/users/?", path),
                Arguments.arguments("/api/test/groups/????/users/??", path),
                Arguments.arguments("/api/test/groups/????/users/???", path),
                Arguments.arguments("/api/test/groups/????/users/????", path),
                Arguments.arguments("/api/test/groups/????/users/?????", path),
                Arguments.arguments("/api/test/groups/?????/users/????", path),
                Arguments.arguments("/api/test/groups/?/*/?", path),
                Arguments.arguments("/api/test/groups/?/*/??", path),
                Arguments.arguments("/api/test/groups/?/*/???", path),
                Arguments.arguments("/api/test/groups/?/*/????", path),
                Arguments.arguments("/api/test/groups/?/*/?????", path),
                Arguments.arguments("/api/test/groups/??/*/?", path),
                Arguments.arguments("/api/test/groups/??/*/??", path),
                Arguments.arguments("/api/test/groups/??/*/???", path),
                Arguments.arguments("/api/test/groups/??/*/????", path),
                Arguments.arguments("/api/test/groups/??/*/?????", path),
                Arguments.arguments("/api/test/groups/???/*/?", path),
                Arguments.arguments("/api/test/groups/???/*/??", path),
                Arguments.arguments("/api/test/groups/???/*/???", path),
                Arguments.arguments("/api/test/groups/???/*/????", path),
                Arguments.arguments("/api/test/groups/???/*/?????", path),
                Arguments.arguments("/api/test/groups/????/*/?", path),
                Arguments.arguments("/api/test/groups/????/*/??", path),
                Arguments.arguments("/api/test/groups/????/*/???", path),
                Arguments.arguments("/api/test/groups/????/*/????", path),
                Arguments.arguments("/api/test/groups/????/*/?????", path),
                Arguments.arguments("/api/test/groups/?????/*/????", path)
        );
    }

    @BeforeEach
    public void setup() {
        antPathMatcher = new AntPathMatcher();
    }

    @Test
    public void testVariableExtraction() {
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        Map<String, String> match = antPathMatcher.extractUriTemplateVariables("/api/test/{id}", "/api/test/test");

        assertNotNull(match.get("id"));
        assertEquals(match.get("id"), "test");
    }

    @Test
    public void testOrdering() {
        // Arrange
        String path = "/api/test/groups/12345/users/12345";
        List<String> paths = new ArrayList<>(List.of(
                "/api/**",
                "/api/test/groups/*/users/12345",
                "/not-matching",
                "/api/test/**",
                "/api/test/groups/**",
                "/api/test/groups",
                "/api/test/groups/12345/users/12345",
                "/**"
        ));
        Comparator<String> comparator = antPathMatcher.getPatternComparator(path);

        // Act
        paths.sort(comparator);

        // Assert
        assertThat(paths).containsExactly(
                "/api/test/groups/12345/users/12345",
                "/api/test/groups",
                "/not-matching",
                "/api/test/groups/*/users/12345",
                "/api/test/groups/**",
                "/api/test/**",
                "/api/**",
                "/**"
        );
    }

    @ParameterizedTest
    @MethodSource("shouldMatch")
    public void testThatPositiveMatchesApply(String pattern, String test) {
        // Act
        boolean match = antPathMatcher.matches(pattern, test);

        // Assert
        assertTrue(match);
    }

    @ParameterizedTest
    @MethodSource("shouldNotMatch")
    public void testThatNegativeMatchesDoNotApply(String pattern, String test) {
        // Act
        boolean match = antPathMatcher.matches(pattern, test);

        // Assert
        assertFalse(match);
    }
}
