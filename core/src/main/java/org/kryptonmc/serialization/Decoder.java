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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/Decoder.java
 */
package org.kryptonmc.serialization;

import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.FieldDecoder;

/**
 * A decoder that can transform some input data in to a standard type.
 *
 * @param <A> The value type.
 */
@FunctionalInterface
public interface Decoder<A> {

    /**
     * Creates a new decoder that always returns the given instance, regardless
     * of the input.
     *
     * @param instance The instance to always return.
     * @param <A> The value type.
     * @return A new unit decoder.
     */
    static <A> @NotNull MapDecoder<A> unit(final @NotNull A instance) {
        return unit(() -> instance);
    }

    /**
     * Creates a new decoder that always returns the result of applying the
     * given instance supplier, regardless of the input.
     *
     * @param instance The instance supplier to always return the result of.
     * @param <A> The value type.
     * @return A new unit decoder.
     */
    static <A> @NotNull MapDecoder<A> unit(final @NotNull Supplier<A> instance) {
        return new MapDecoder<>() {
            @Override
            public <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return instance.get();
            }

            @Override
            public String toString() {
                return "UnitDecoder[" + instance.get() + "]";
            }
        };
    }

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
    <T> A decode(final T input, final @NotNull DataOps<T> ops);

    /**
     * Creates a new decoder that decodes a field with the given name using
     * this decoder to decode the value of the field.
     *
     * @param name The name of the field.
     * @return A new field decoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapDecoder<A> field(final @NotNull String name) {
        return new FieldDecoder<>(name, this);
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
    default <B> @NotNull Decoder<B> map(final @NotNull Function<? super A, ? extends B> function) {
        return new Decoder<>() {
            @Override
            public <T> B decode(final T input, final @NotNull DataOps<T> ops) {
                return function.apply(Decoder.this.decode(input, ops));
            }

            @Override
            public String toString() {
                return Decoder.this + "[mapped]";
            }
        };
    }
}
