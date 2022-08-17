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
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.nbt.ByteArrayTag;
import org.kryptonmc.nbt.ByteTag;
import org.kryptonmc.nbt.CollectionTag;
import org.kryptonmc.nbt.EndTag;
import org.kryptonmc.nbt.IntArrayTag;
import org.kryptonmc.nbt.IntTag;
import org.kryptonmc.nbt.ListTag;
import org.kryptonmc.nbt.LongArrayTag;
import org.kryptonmc.nbt.LongTag;
import org.kryptonmc.nbt.Tag;

final class NbtUtil {

    // This is consistent with vanilla's NbtOps.
    public static @NotNull CollectionTag<?> createGenericList(final @NotNull Tag input, final int targetElementType) {
        final int inputElementType = input instanceof final CollectionTag<?> tag ? tag.getElementType() : EndTag.ID;
        if (typesMatch(inputElementType, targetElementType, ByteTag.ID)) return new ByteArrayTag(new byte[0]);
        if (typesMatch(inputElementType, targetElementType, IntTag.ID)) return new IntArrayTag(new int[0]);
        if (typesMatch(inputElementType, targetElementType, LongTag.ID)) return new LongArrayTag(new long[0]);
        return ListTag.mutable(new ArrayList<>(), EndTag.ID);
    }

    private static boolean typesMatch(final int inputElementType, final int targetElementType, final int resultElementType) {
        return inputElementType == resultElementType && (targetElementType == inputElementType || targetElementType == 0);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Tag> void fillOne(final @NotNull CollectionTag<T> result, final @NotNull Tag input, final @NotNull Tag value) {
        if (input instanceof final CollectionTag<?> tag) tag.forEach(element -> result.addTag((T) element));
        result.addTag((T) value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Tag> void fillMany(final @NotNull CollectionTag<T> result, final @NotNull Tag input, final @NotNull List<Tag> values) {
        if (input instanceof final CollectionTag<?> tag) tag.forEach(element -> result.addTag((T) element));
        values.forEach(element -> result.addTag((T) element));
    }

    // This is a classic. ByteBuffers can be heap or direct, and because of how direct buffers work, there isn't an intermediary
    // array for us to retrieve. Therefore, we have to extract the bytes manually in to our own array.
    @SuppressWarnings("ByteBufferBackingArray")
    public static byte@NotNull[] toArray(final @NotNull ByteBuffer input) {
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
