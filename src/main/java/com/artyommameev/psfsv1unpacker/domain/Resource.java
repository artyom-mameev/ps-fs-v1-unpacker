package com.artyommameev.psfsv1unpacker.domain;

import lombok.NonNull;
import org.apache.commons.io.FileUtils;

/**
 * Encapsulates an archived resource file.
 *
 * @author Artyom Mameev
 */
public class Resource {

    private final String name;
    private final int size;
    private final int offset;

    /**
     * Instantiates a new Resource.
     *
     * @param name   a name of the resource.
     * @param size   a size of the resource.
     * @param offset a resource offset in the archive file.
     * @throws NullPointerException     if the name is null.
     * @throws IllegalArgumentException if the name is empty,
     *                                  or if the size or the offset <= 0.
     */
    public Resource(@NonNull String name, int size, int offset) {
        if (!name.isEmpty()) {
            this.name = name;
        } else throw new IllegalArgumentException("Name cannot be empty");

        if (size > 0) {
            this.size = size;
        } else throw new IllegalArgumentException("Size cannot be <= 0");

        if (offset > 0) {
            this.offset = offset;
        } else throw new IllegalArgumentException("Offset cannot be <= 0");
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Returns a formatted size of the resource to display in the UI.
     *
     * @return a formatted size of the resource.
     */
    public String getFormattedSize() {
        return FileUtils.byteCountToDisplaySize(size);
    }

    /**
     * Returns a string representation of the Resource in the following format:
     * <p>
     * name (formatted size)
     */
    @Override
    public String toString() {
        return name + " (" + getFormattedSize() + ")";
    }
}


