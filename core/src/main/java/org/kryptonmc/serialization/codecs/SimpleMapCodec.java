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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/SimpleMapCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;

/**
 * A simple map codec implementation that processes a map of values.
 *
 * @param keyCodec The key codec.
 * @param valueCodec The value codec.
 * @param <K> The key type.
 * @param <V> The value type.
 */
public record SimpleMapCodec<K, V>(@NotNull Codec<K> keyCodec, @NotNull Codec<V> valueCodec) implements BaseMapCodec<K, V>, MapCodec<Map<K, V>> {

    @SuppressWarnings("MissingJavadocMethod")
    public SimpleMapCodec {
        Objects.requireNonNull(keyCodec, "keyCodec");
        Objects.requireNonNull(valueCodec, "valueCodec");
    }

    @Override
    public <T> @NotNull DataResult<Map<K, V>> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        return BaseMapCodec.super.decode(input, ops);
    }

    @Override
    public <T> @NotNull RecordBuilder<T> encode(final @NotNull Map<K, V> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        return BaseMapCodec.super.encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "SimpleMapCodec[" + keyCodec + " -> " + valueCodec + "]";
    }
}
