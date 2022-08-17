/*
 * This file is part of Krypton Serialization, and originates from the Data
 * Fixer Upper, licensed under the MIT license.
 *
 * Copyright (C) Microsoft Corporation. All rights reserved.
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 *
 * For the original file that this file is derived from, see here:
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/FieldDecoder.java
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
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
    public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var value = input.get(name);
        if (value == null) return DataResult.error("No key " + name + "found in map " + input + "!");
        return elementDecoder.read(value, ops);
    }

    @Override
    public String toString() {
        return "FieldDecoder[" + name + ": " + elementDecoder + ']';
    }
}
