package com.wiredi.web;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PathsTest {

    @ParameterizedTest
    @MethodSource("allPotentialArguments")
    public void testPathResolving(
            @NotNull URI uri,
            @Nullable String contextPath,
            @NotNull String expected
    ) {
        // Act
        String path = Paths.resolvePath(uri, contextPath);

        // Assert
        assertEquals(expected, path);
    }

    public static List<Arguments> allPotentialArguments() {
        URI uriCaseOne = URI.create("http://localhost/test/servlet/groups/12345/users/12345");
        return List.of(
                // "Ignored" cases that do not alter the pattern
                arguments(uriCaseOne, null, "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "", "/test/servlet/groups/12345/users/12345"),

                // Non-Matches that do not alter the pattern
                arguments(uriCaseOne, "/non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/non-existing", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "non-existing", "/test/servlet/groups/12345/users/12345"),

                arguments(uriCaseOne, "/test/non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/test/non-existing", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/non-existing", "/test/servlet/groups/12345/users/12345"),

                arguments(uriCaseOne, "/test/servlet/non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/test/servlet/non-existing", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet/non-existing/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet/non-existing", "/test/servlet/groups/12345/users/12345"),

                arguments(uriCaseOne, "/test/non-existing/servlet/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/test/non-existing/servlet", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/non-existing/servlet/", "/test/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/non-existing/servlet", "/test/servlet/groups/12345/users/12345"),

                // Matches that remove the context pattern
                arguments(uriCaseOne, "/test/", "/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/", "/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "/test", "/servlet/groups/12345/users/12345"),
                arguments(uriCaseOne, "test", "/servlet/groups/12345/users/12345"),

                arguments(uriCaseOne, "/test/servlet/", "/groups/12345/users/12345"),
                arguments(uriCaseOne, "/test/servlet", "/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet/", "/groups/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet", "/groups/12345/users/12345"),

                arguments(uriCaseOne, "/test/servlet/groups/", "/12345/users/12345"),
                arguments(uriCaseOne, "/test/servlet/groups", "/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet/groups/", "/12345/users/12345"),
                arguments(uriCaseOne, "test/servlet/groups", "/12345/users/12345")
        );
    }
}
