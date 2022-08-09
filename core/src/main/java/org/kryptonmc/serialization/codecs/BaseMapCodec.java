/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

public interface BaseMapCodec<K, V> {

    @NotNull Codec<K> keyCodec();

    @NotNull Codec<V> valueCodec();

    default <T> @NotNull Map<K, V> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var read = new HashMap<K, V>();
        final var failed = new ArrayList<Pair<T, T>>();
        input.entries().forEach(entry -> {
            try {
                read.put(keyCodec().decode(entry.first(), ops), valueCodec().decode(entry.second(), ops));
            } catch (final Exception ignored) {
                failed.add(entry);
            }
        });
        if (!failed.isEmpty()) throw new IllegalArgumentException("Failed to decode map! Failed input: " + failed);
        return Map.copyOf(read);
    }

    default <T> @NotNull RecordBuilder<T> encode(final @NotNull Map<K, V> input, final @NotNull DataOps<T> ops,
                                                 final @NotNull RecordBuilder<T> prefix) {
        for (final var entry : input.entrySet()) {
            prefix.add(keyCodec().encodeStart(entry.getKey(), ops), valueCodec().encodeStart(entry.getValue(), ops));
        }
        return prefix;
    }
}
