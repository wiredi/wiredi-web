package com.wiredi.web.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArraysTest {

    @Test
    public void prependWorks() {
        // Arrange
        byte[] base = {4, 5, 6};
        byte[] prefix = {1, 2, 3};

        // Act
        byte[] result = Arrays.prepend(base, prefix);

        // Assert
        assertArrayEquals(result, new byte[] {1, 2, 3, 4, 5, 6});
    }

    @Test
    public void appendWorks() {
        // Arrange
        byte[] base = {4, 5, 6};
        byte[] appendix = {1, 2, 3};

        // Act
        byte[] result = Arrays.append(base, appendix);

        // Assert
        assertArrayEquals(result, new byte[] {4, 5, 6, 1, 2, 3});
    }

}