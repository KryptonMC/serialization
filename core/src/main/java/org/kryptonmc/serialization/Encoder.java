/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.FieldEncoder;
import org.kryptonmc.serialization.codecs.PairCodec;

/**
 * An encoder that can transform some input type in to generic data.
 *
 * @param <A> The value type.
 */
@FunctionalInterface
public interface Encoder<A> {

    /**
     * Gets the empty map encoder, a map encoder that always returns the prefix
     * provided to it, and ignores the input.
     *
     * @param <A> The value type.
     * @return The empty map encoder.
     */
    @SuppressWarnings("unchecked")
    static <A> @NotNull MapEncoder<A> empty() {
        return (MapEncoder<A>) CodecUtil.EMPTY;
    }

    /**
     * Encodes the given input type to the given output data type using the
     * given operations to convert the input in to data type values.
     *
     * <p>The prefix that is provided is for chaining codecs together to allow,
     * for example, codecs such as the {@link PairCodec}, to encode both values
     * of the pair to the same type.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param prefix The prefix to append the results to, for easy chaining of
     *               codecs. See above.
     * @param <T> The data type.
     * @return The encoded value.
     */
    <T> T encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix);

    /**
     * Encodes the given input type to the given output data type using the
     * given operations to convert the input in to data type values.
     *
     * <p>This is equivalent to calling {@link #encode(Object, DataOps, Object)}
     * with a prefix of {@link DataOps#empty()}, and is provided as a helper
     * function if the prefix does not matter.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The encoded value.
     */
    @ApiStatus.NonExtendable
    default <T> T encodeStart(final A input, final @NotNull DataOps<T> ops) {
        return encode(input, ops, ops.empty());
    }

    /**
     * Creates a new encoder that encodes a field with the given name using
     * this encoder to encode the value of the field.
     *
     * @param name The name of the field.
     * @return A new field encoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapEncoder<A> field(final @NotNull String name) {
        return new FieldEncoder<>(name, this);
    }

    /**
     * Maps this encoder to a new encoder, using the given function to map
     * results from this encoder to a new type for the new encoder.
     *
     * <p>This method is called {@code comap} because we want to be able to
     * differentiate it from {@link Decoder#map(Function)}.</p>
     *
     * @param function The function to apply when mapping the encoded value
     *                 from this encoder.
     * @param <B> The new value type.
     * @return The resulting mapped encoder.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Encoder<B> comap(final @NotNull Function<? super B, ? extends A> function) {
        return new Encoder<>() {
            @Override
            public <T> T encode(final B input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return Encoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public String toString() {
                return Encoder.this + "[comapped]";
            }
        };
    }
}
