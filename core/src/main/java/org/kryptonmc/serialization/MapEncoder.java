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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/MapEncoder.java
 */
package org.kryptonmc.serialization;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * An encoder that can transform some input type in to a generic map-like
 * structure.
 *
 * @param <A> The value type.
 */
@FunctionalInterface
public interface MapEncoder<A> {

    /**
     * Encodes the given input type to the given output data type using the
     * given operations to convert the input in to data type values.
     *
     * <p>The prefix is provided as the existing data that this method should
     * append the data it encodes to.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param prefix The prefix to append the results to.
     * @param <T> The data type.
     * @return The encoded value.
     */
    <T> @NotNull RecordBuilder<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix);

    /**
     * Maps this map encoder to a new map encoder, using the given function to
     * map results from this encoder to a new type for the new map encoder.
     *
     * <p>This method is called {@code comap} because we want to be able to
     * differentiate it from {@link MapDecoder#map(Function)}.</p>
     *
     * @param function The function to apply when mapping the encoded value
     *                 from this map encoder.
     * @param <B> The new value type.
     * @return The resulting mapped encoder.
     */
    default <B> @NotNull MapEncoder<B> comap(final @NotNull Function<? super B, ? extends A> function) {
        return new MapEncoder<>() {
            @Override
            public @NotNull <T> RecordBuilder<T> encode(final B input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
                return MapEncoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public String toString() {
                return MapEncoder.this + "[comapped]";
            }
        };
    }

    /**
     * Creates a new encoder that delegates to this encoder.
     *
     * <p>This method acts as a bridge between the standard {@link Encoder} and
     * the map-based {@link MapEncoder} (this type).</p>
     *
     * @return This encoder as a standard encoder.
     */
    default @NotNull Encoder<A> encoder() {
        return new Encoder<>() {
            @Override
            public <T> T encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return MapEncoder.this.encode(input, ops, ops.mapBuilder()).build(prefix);
            }

            @Override
            public String toString() {
                return MapEncoder.this.toString();
            }
        };
    }
}
