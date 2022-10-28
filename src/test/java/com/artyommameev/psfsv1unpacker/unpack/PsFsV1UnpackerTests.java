package com.artyommameev.psfsv1unpacker.unpack;

import com.artyommameev.psfsv1unpacker.domain.Resource;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"ResultOfMethodCallIgnored", "deprecation", "ConstantConditions"})
public class PsFsV1UnpackerTests {

    private PsFsV1Unpacker psFsV1Unpacker;

    @BeforeEach
    void setUp() throws IOException {
        val testResourceUrl = getClass().getResource("test.dat");
        psFsV1Unpacker = new PsFsV1Unpacker(new File(testResourceUrl.getPath()));
    }

    @Test
    void constructorThrowsNullPointerExceptionIfFileIsNull() {
        assertThrows(NullPointerException.class, () ->
                psFsV1Unpacker = new PsFsV1Unpacker(null));
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionIfHeaderIsWrong() {
        val testWrongHeaderResourceUrl = getClass().getResource(
                "test_wrong_header.dat");

        assertThrows(IllegalArgumentException.class, () -> psFsV1Unpacker =
                new PsFsV1Unpacker(new File(testWrongHeaderResourceUrl.getPath())));
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionIfHeaderIsCorrupted() {
        val testCorruptedHeaderResourceUrl = getClass().getResource(
                "test_wrong_header.dat");

        assertThrows(IllegalArgumentException.class, () -> psFsV1Unpacker =
                new PsFsV1Unpacker(new File(testCorruptedHeaderResourceUrl
                        .getPath())));
    }

    @Test
    void getAllResourcesGetsAllResources() throws IOException {
        val resources = psFsV1Unpacker.getAllResources();

        assertEquals(2, resources.size());

        Resource resource = resources.get(0);

        assertEquals("testfile.test", resource.getName());
        assertEquals(8, resource.getSize());
        assertEquals(240, resource.getOffset());

        resource = resources.get(1);

        assertEquals("testfile2.test", resource.getName());
        assertEquals(8, resource.getSize());
        assertEquals(272, resource.getOffset());
    }

    @Test
    void getAllResourcesThrowsIllegalArgumentExceptionIfFileIsCorrupted() {
        val testCorruptedResourceUrl = getClass().getResource(
                "test_wrong_header.dat");

        assertThrows(IllegalArgumentException.class, () -> {
            psFsV1Unpacker = new PsFsV1Unpacker(new File(
                    testCorruptedResourceUrl.getPath()));
            psFsV1Unpacker.getAllResources();
        });
    }

    @Test
    void unpackResourceThrowsNullPointerExceptionIfDirectoryOrResourceIsNull() {
        assertThrows(NullPointerException.class, () -> psFsV1Unpacker.unpackResource(
                null, new Resource("test", 1, 2)));

        assertThrows(NullPointerException.class, () -> psFsV1Unpacker.unpackResource(
                new File("./"), null));
    }

    @Test
    void unpackResourceUnpacksResources() throws IOException {
        for (val resource : psFsV1Unpacker.getAllResources()) {
            psFsV1Unpacker.unpackResource(new File("./"), resource);
        }

        File file = new File("./testfile.test");
        String actual = FileUtils.readFileToString(file);

        assertEquals("FILENO1!", actual);

        file.delete();

        file = new File("./testfile2.test");
        actual = FileUtils.readFileToString(file);

        assertEquals("FILENO2!", actual);

        file.delete();
    }
}