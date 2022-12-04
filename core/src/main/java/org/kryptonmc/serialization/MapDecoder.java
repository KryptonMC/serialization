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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/MapDecoder.java
 */
package org.kryptonmc.serialization;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

/**
 * A decoder that can transform some map-like input structure in to a standard
 * type.
 *
 * @param <A> The value type.
 */
@FunctionalInterface
public interface MapDecoder<A> {

    /**
     * Decodes the given input data to the standard type that this decoder is
     * for, using the given operations to convert the input in to standard
     * types that can be used generically when decoding.
     *
     * @param input The input.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The decoded result.
     */
    <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops);

    /**
     * Decodes the given input data to the standard type that this decoder is
     * for, using the given operations to convert the input in to standard
     * types that can be used generically when decoding.
     *
     * <p>This uses the operations to convert the input to a map-like
     * structure.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The decoded value.
     */
    default <T> @NotNull DataResult<A> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return ops.getMap(input).withLifecycle(Lifecycle.stable()).flatMap(map -> decode(map, ops));
    }

    /**
     * Maps this decoder to a new decoder, using the given function to map
     * results from this decoder to a new type for the new decoder.
     *
     * @param mapper The function to apply when mapping the decoded value from
     *               this decoder.
     * @param <B> The new value type.
     * @return The resulting mapped decoder.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull MapDecoder<B> map(final @NotNull Function<? super A, ? extends B> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new MapDecoder<>() {
            @Override
            public <T> @NotNull DataResult<B> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return MapDecoder.this.decode(input, ops).map(mapper);
            }

            @Override
            public String toString() {
                return MapDecoder.this + "[mapped]";
            }
        };
    }

    /**
     * Maps this decoder to a new decoder, using the given function to map
     * results from this decoder to a new type for the new decoder.
     *
     * <p>This differs from {@link #map(Function)} in that the function returns
     * a {@link DataResult}, allowing errors and partial results to be
     * preserved.</p>
     *
     * @param mapper The function to apply when mapping the decoded value from
     *               this decoder.
     * @param <B> The new value type.
     * @return The resulting mapped decoder.
     */
    default <B> @NotNull MapDecoder<B> flatMap(final @NotNull Function<? super A, ? extends DataResult<? extends B>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new MapDecoder<>() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> @NotNull DataResult<B> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return MapDecoder.this.decode(input, ops).flatMap(result -> (DataResult<B>) mapper.apply(result));
            }

            @Override
            public String toString() {
                return MapDecoder.this + "[flatMapped]";
            }
        };
    }

    /**
     * Creates a new decoder that sets the lifecycle of all decoded data
     * results to the given lifecycle.
     *
     * @param lifecycle The lifecycle.
     * @return A new map decoder.
     */
    default @NotNull MapDecoder<A> withLifecycle(final @NotNull Lifecycle lifecycle) {
        Objects.requireNonNull(lifecycle, "lifecycle");
        return new MapDecoder<>() {
            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return MapDecoder.this.decode(input, ops).withLifecycle(lifecycle);
            }

            @Override
            public String toString() {
                return MapDecoder.this.toString();
            }
        };
    }

    /**
     * Creates a new decoder that delegates to this map decoder.
     *
     * <p>This method acts as a bridge between the standard {@link Decoder} and
     * the map-based {@link MapDecoder} (this type).</p>
     *
     * @return This encoder as a standard decoder.
     */
    default @NotNull Decoder<A> decoder() {
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return MapDecoder.this.decode(input, ops).map(result -> Pair.of(result, input));
            }

            @Override
            public String toString() {
                return MapDecoder.this.toString();
            }
        };
    }
}
