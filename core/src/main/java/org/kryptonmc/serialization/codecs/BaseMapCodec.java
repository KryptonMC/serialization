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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/BaseMapCodec.java
 */
package org.kryptonmc.serialization.codecs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Lifecycle;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;
import org.kryptonmc.util.Unit;

/**
 * The base map codec implementation that contains the common logic for the
 * simple and unbounded map codecs.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public sealed interface BaseMapCodec<K, V> permits SimpleMapCodec, UnboundedMapCodec {

    /**
     * Gets the codec used to encode and decode the keys of the map.
     *
     * @return The key codec.
     */
    @NotNull Codec<K> keyCodec();

    /**
     * Gets the codec used to encode and decode the values of the map.
     *
     * @return The value codec.
     */
    @NotNull Codec<V> valueCodec();

    /**
     * Decodes the input map to an immutable map with the given ops.
     *
     * @param input The input map to decode.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The decoded map.
     */
    default <T> @NotNull DataResult<Map<K, V>> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder();

        final DataResult<Unit> result = input.entries().reduce(DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (r, pair) -> {
            final var k = keyCodec().read(pair.first(), ops);
            final var v = valueCodec().read(pair.second(), ops);

            final var entry = k.apply2stable(Pair::of, v);
            entry.error().ifPresent(e -> failed.add(pair));

            return r.apply2stable((u, p) -> {
                read.put(p.first(), p.second());
                return u;
            }, entry);
        }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2));

        final Map<K, V> elements = read.build();
        final var errors = ops.createMap(failed.build().stream());
        return result.map(unit -> elements).withPartial(elements).mapError(error -> error + " missed input: " + errors);
    }

    /**
     * Encodes the input map by appending all the entries to the prefix record
     * builder for the data type and returning the resulting record builder.
     *
     * @param input The input map.
     * @param ops The data operations.
     * @param prefix The record builder to append the map entries to.
     * @param <T> The data type.
     * @return The resulting record builder.
     */
    default <T> @NotNull RecordBuilder<T> encode(final @NotNull Map<K, V> input, final @NotNull DataOps<T> ops,
                                                 final @NotNull RecordBuilder<T> prefix) {
        for (final var entry : input.entrySet()) {
            prefix.add(keyCodec().encodeStart(entry.getKey(), ops), valueCodec().encodeStart(entry.getValue(), ops));
        }
        return prefix;
    }
}
