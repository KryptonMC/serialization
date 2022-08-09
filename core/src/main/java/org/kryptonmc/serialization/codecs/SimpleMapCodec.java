/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;

public record SimpleMapCodec<K, V>(@NotNull Codec<K> keyCodec, @NotNull Codec<V> valueCodec) implements BaseMapCodec<K, V>, MapCodec<Map<K, V>> {

    @Override
    public @NotNull <T> Map<K, V> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        return BaseMapCodec.super.decode(input, ops);
    }

    @Override
    public @NotNull <T> RecordBuilder<T> encode(final @NotNull Map<K, V> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        return BaseMapCodec.super.encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "SimpleMapCodec[" + keyCodec + " -> " + valueCodec + "]";
    }
}
