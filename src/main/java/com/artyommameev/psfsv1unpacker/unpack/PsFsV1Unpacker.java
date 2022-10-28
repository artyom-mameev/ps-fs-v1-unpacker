package com.artyommameev.psfsv1unpacker.unpack;

import com.artyommameev.psfsv1unpacker.domain.Resource;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.val;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unpacks {@link Resource}s from a 'PS_FS_V1' archive.
 *
 * @author Artyom Mameev
 */
public class PsFsV1Unpacker {

    private final static byte[] HEADER = new byte[8];
    private final static byte[] RESOURCES_COUNT = new byte[4];
    private final static byte[] SEPARATOR = new byte[4];
    private final static byte[] BUFFER = new byte[26];

    private final static byte[] CORRECT_HEADER = {0x50, 0x53, 0x5F, 0x46, 0x53,
            0x5F, 0x56, 0x31};

    private final static int USELESS_HEADER_SIZE = 16;

    private final File file;
    private final DataInputStream dataInputStream;
    private List<Resource> resources;

    /**
     * Instantiates a new PsFsV1Unpacker.
     *
     * @param file the 'PS_FS_V1' archive.
     * @throws NullPointerException     if the file is null.
     * @throws IllegalArgumentException if the file has a wrong header.
     * @throws IOException              if the file header is corrupted.
     */
    public PsFsV1Unpacker(@NonNull File file) throws java.io.IOException {
        this.file = file;

        dataInputStream = new DataInputStream(new FileInputStream(file));

        if (dataInputStream.read(HEADER) == -1)
            throw new IOException("End of file is reached");
        if (!Arrays.equals(HEADER, CORRECT_HEADER))
            throw new IllegalArgumentException("Wrong Header");
        if (dataInputStream.read(RESOURCES_COUNT) == -1)
            throw new IOException("End of file is reached");
        if (dataInputStream.read(SEPARATOR) == -1)
            throw new IOException("End of file is reached");
    }

    /**
     * Returns all {@link Resource}s in the archive.
     *
     * @return the list of all {@link Resource}s in the archive file.
     * @throws IOException if the file is corrupted.
     */
    public List<Resource> getAllResources() throws java.io.IOException {
        if (resources != null) {
            return resources;
        }

        resources = new ArrayList<>();

        for (int i = 0; i < getResourceCount(); i++) {
            byte[] nameBytes = new byte[22];
            byte[] sizeBytes = new byte[4];
            byte[] offsetBytes = new byte[4];

            if (dataInputStream.read(nameBytes) == -1)
                throw new IOException("End of file is reached.");
            if (dataInputStream.read(BUFFER) == -1)
                throw new IOException("End of file is reached.");
            if (dataInputStream.read(sizeBytes) == -1)
                throw new IOException("End of file is reached.");
            if (dataInputStream.read(SEPARATOR) == -1)
                throw new IOException("End of file is reached.");
            if (dataInputStream.read(offsetBytes) == -1)
                throw new IOException("End of file is reached.");
            if (dataInputStream.read(SEPARATOR) == -1)
                throw new IOException("End of file is reached.");

            val name = new String(nameBytes, StandardCharsets.UTF_8)
                    .trim();

            int size = ByteBuffer.wrap(sizeBytes)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt() - USELESS_HEADER_SIZE;

            int offset = ByteBuffer.wrap(offsetBytes)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt() + USELESS_HEADER_SIZE;

            resources.add(new Resource(name, size, offset));
        }

        dataInputStream.close();

        return resources;
    }

    /**
     * Unpacks a {@link Resource} to a selected directory.
     *
     * @param directory the directory to which the {@link Resource} should be
     *                  unpacked.
     * @param resource  the {@link Resource} that should be unpacked.
     * @throws IOException          if the file is corrupted.
     * @throws NullPointerException if the directory or the resource is null.
     */
    public void unpackResource(@NonNull File directory,
                               @NonNull Resource resource)
            throws java.io.IOException {
        @Cleanup val randomAccessFile = new RandomAccessFile(file, "r");

        byte[] data = new byte[resource.getSize()];

        randomAccessFile.seek(resource.getOffset());

        for (int i = 0; i < resource.getSize(); i++) {
            data[i] = randomAccessFile.readByte();
        }

        val path = Paths.get(directory + "/" + resource.getName());

        Files.write(path, data);
    }

    private int getResourceCount() {
        return ByteBuffer.wrap(RESOURCES_COUNT)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }
}

