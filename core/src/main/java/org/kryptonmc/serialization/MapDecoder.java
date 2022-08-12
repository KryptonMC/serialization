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

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
     * @return The decoded value.
     */
    <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops);

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
    default <T> A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return decode(ops.getMap(input), ops);
    }

    /**
     * Maps this decoder to a new decoder, using the given function to map
     * results from this decoder to a new type for the new decoder.
     *
     * @param function The function to apply when mapping the decoded value
     *                 from this decoder.
     * @param <B> The new value type.
     * @return The resulting mapped decoder.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull MapDecoder<B> map(final @NotNull Function<? super A, ? extends B> function) {
        return new MapDecoder<>() {
            @Override
            public <T> B decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return function.apply(MapDecoder.this.decode(input, ops));
            }

            @Override
            public String toString() {
                return MapDecoder.this + "[mapped]";
            }
        };
    }
}
