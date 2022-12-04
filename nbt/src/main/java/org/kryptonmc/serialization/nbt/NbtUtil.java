/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.nbt;

import java.nio.ByteBuffer;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.nbt.ByteArrayTag;
import org.kryptonmc.nbt.ByteTag;
import org.kryptonmc.nbt.CollectionTag;
import org.kryptonmc.nbt.EndTag;
import org.kryptonmc.nbt.IntArrayTag;
import org.kryptonmc.nbt.IntTag;
import org.kryptonmc.nbt.LongArrayTag;
import org.kryptonmc.nbt.LongTag;
import org.kryptonmc.nbt.MutableListTag;
import org.kryptonmc.nbt.Tag;

final class NbtUtil {

    // This is consistent with vanilla's NbtOps.
    static CollectionTag<?> createGenericList(final Tag input, final int targetType) {
        final int inputType = input instanceof final CollectionTag<?> tag ? tag.elementType() : EndTag.ID;
        if (typesMatch(inputType, targetType, ByteTag.ID)) return ByteArrayTag.of(new byte[0]);
        if (typesMatch(inputType, targetType, IntTag.ID)) return IntArrayTag.of(new int[0]);
        if (typesMatch(inputType, targetType, LongTag.ID)) return LongArrayTag.of(new long[0]);
        return MutableListTag.empty();
    }

    private static boolean typesMatch(final int inputElementType, final int targetElementType, final int resultElementType) {
        return inputElementType == resultElementType && (targetElementType == inputElementType || targetElementType == 0);
    }

    @SuppressWarnings("unchecked")
    public static <T extends @NotNull Tag> void fillOne(final CollectionTag<T> result, final Tag input, final Tag value) {
        if (input instanceof final CollectionTag<?> tag) tag.forEach(element -> result.tryAdd((T) element));
        result.tryAdd((T) value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends @NotNull Tag> void fillMany(final CollectionTag<T> result, final Tag input, final List<Tag> values) {
        if (input instanceof final CollectionTag<?> tag) tag.forEach(element -> result.tryAdd((T) element));
        values.forEach(element -> result.tryAdd((T) element));
    }

    // This is a classic. ByteBuffers can be heap or direct, and because of how direct buffers work, there isn't an intermediary
    // array for us to retrieve. Therefore, we have to extract the bytes manually in to our own array.
    @SuppressWarnings("ByteBufferBackingArray")
    public static byte[] toArray(final ByteBuffer input) {
        final byte[] result;
        if (input.hasArray()) {
            result = input.array();
        } else {
            result = new byte[input.capacity()];
            input.get(result, 0, result.length);
        }
        return result;
    }

    private NbtUtil() {
    }
}
