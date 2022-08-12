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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/ListCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;

/**
 * A codec that will encode a list of values by encoding each value with the
 * given element codec, and decode an input to a list of values by decoding
 * each value with the given element codec.
 *
 * @param elementCodec The element codec.
 * @param <A> The element type.
 */
public record ListCodec<A>(@NotNull Codec<A> elementCodec) implements Codec<List<A>> {

    @Override
    public <T> @NotNull List<A> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        final var stream = ops.getList(input);
        final var result = new ArrayList<A>();
        final var failed = new ArrayList<T>();
        stream.accept(value -> {
            try {
                result.add(elementCodec.decode(value, ops));
            } catch (final Exception ignored) {
                failed.add(value);
            }
        });
        if (!failed.isEmpty()) throw new IllegalArgumentException("Failed to decode list! Failed input: " + failed);
        return List.copyOf(result);
    }

    @Override
    public <T> @NotNull T encode(final @NotNull List<A> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        final var builder = ops.listBuilder();
        for (final var value : input) {
            builder.add(elementCodec.encodeStart(value, ops));
        }
        return builder.build(prefix);
    }

    @Override
    public String toString() {
        return "ListCodec[" + elementCodec + ']';
    }
}
