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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/UnboundedMapCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Lifecycle;
import org.kryptonmc.util.Pair;

/**
 * A simple codec that processes a map of values.
 *
 * @param keyCodec The key codec.
 * @param valueCodec The value codec.
 * @param <K> The key type.
 * @param <V> The value type.
 */
public record UnboundedMapCodec<K, V>(@NotNull Codec<K> keyCodec, @NotNull Codec<V> valueCodec) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {

    @SuppressWarnings("MissingJavadocMethod")
    public UnboundedMapCodec {
        Objects.requireNonNull(keyCodec, "keyCodec");
        Objects.requireNonNull(valueCodec, "valueCodec");
    }

    @Override
    public <T> @NotNull DataResult<Pair<Map<K, V>, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return ops.getMap(input).withLifecycle(Lifecycle.stable()).flatMap(map -> decode(map, ops)).map(result -> Pair.of(result, input));
    }

    @Override
    public <T> @NotNull DataResult<T> encode(final @NotNull Map<K, V> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public String toString() {
        return "UnboundedMapCodec[" + keyCodec + " -> " + valueCodec + ']';
    }
}
