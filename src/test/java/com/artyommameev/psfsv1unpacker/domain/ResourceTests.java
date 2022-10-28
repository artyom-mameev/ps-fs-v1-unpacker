package com.artyommameev.psfsv1unpacker.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ConstantConditions")
public class ResourceTests {

    private Resource resource;

    @Test
    void constructorThrowsNullPointerExceptionIfNameIsNull() {
        assertThrows(NullPointerException.class, () -> resource =
                new Resource(null, 1, 2));
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionIfNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> resource =
                new Resource("", 1, 2));
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionIfSizeIsZero() {
        assertThrows(IllegalArgumentException.class, () -> resource =
                new Resource("Name", 0, 2));
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionIfOffsetIsZero() {
        assertThrows(IllegalArgumentException.class, () -> resource =
                new Resource("Name", 1, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, -Integer.MAX_VALUE})
    void constructorThrowsIllegalArgumentExceptionIfSizeIsNegative(int size) {
        assertThrows(IllegalArgumentException.class, () -> resource =
                new Resource("Name", size, 2));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, -Integer.MAX_VALUE})
    void constructorThrowsIllegalArgumentExceptionIfOffsetIsNegative(int offset) {
        assertThrows(IllegalArgumentException.class, () -> resource =
                new Resource("Name", 1, offset));
    }

    @Test
    void toStringReturnsCorrectStringRepresentation() {
        resource = new Resource("Name", 1024, 2);

        assertEquals("Name (1 KB)", resource.toString());
    }
}