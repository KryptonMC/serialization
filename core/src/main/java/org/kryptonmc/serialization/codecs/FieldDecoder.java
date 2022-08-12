/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.Decoder;
import org.kryptonmc.serialization.MapDecoder;
import org.kryptonmc.serialization.MapLike;

/**
 * A map decoder that will attempt to decode a field with the given name from
 * an input map, decoding the value mapped to the name (key) in the map with
 * the element decoder.
 *
 * @param name The field name.
 * @param elementDecoder The field value decoder.
 * @param <A> The output type.
 */
public record FieldDecoder<A>(@NotNull String name, @NotNull Decoder<A> elementDecoder) implements MapDecoder<A> {

    @Override
    public <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var value = input.get(name);
        if (value == null) throw new IllegalArgumentException("No key " + name + " found in map " + input);
        return elementDecoder.decode(value, ops);
    }

    @Override
    public String toString() {
        return "FieldDecoder[" + name + ": " + elementDecoder + ']';
    }
}
