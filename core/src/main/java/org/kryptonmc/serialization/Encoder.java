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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/Encoder.java
 */
package org.kryptonmc.serialization;

import java.util.Objects;
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
     * Creates a new encoder that always returns a result with the given error
     * message.
     *
     * @param error The error message.
     * @param <A> The value type.
     * @return A new error encoder.
     */
    static <A> @NotNull Encoder<A> error(final @NotNull String error) {
        Objects.requireNonNull(error, "error");
        return new Encoder<>() {
            @Override
            public <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return DataResult.error(error);
            }

            @Override
            public String toString() {
                return "ErrorEncoder[" + error + "]";
            }
        };
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
    <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix);

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
    default <T> @NotNull DataResult<T> encodeStart(final A input, final @NotNull DataOps<T> ops) {
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
    default @NotNull MapEncoder<A> fieldOf(final @NotNull String name) {
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
        Objects.requireNonNull(function, "function");
        return new Encoder<>() {
            @Override
            public <T> @NotNull DataResult<T> encode(final B input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return Encoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public String toString() {
                return Encoder.this + "[comapped]";
            }
        };
    }

    /**
     * Maps this encoder to a new encoder, using the given function to map
     * results from this encoder to a new type for the new encoder.
     *
     * <p>This method is called {@code flatComap} because we want to be able to
     * differentiate it from {@link Decoder#flatMap(Function)}.</p>
     *
     * <p>This method is different to {@link #comap(Function)} in that the
     * function returns a {@link DataResult}.</p>
     *
     * @param function The function to apply when mapping the encoded value
     *                 from this encoder.
     * @param <B> The new value type.
     * @return The resulting mapped encoder.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Encoder<B> flatComap(final @NotNull Function<? super B, ? extends DataResult<? extends A>> function) {
        Objects.requireNonNull(function, "function");
        return new Encoder<>() {
            @Override
            public <T> @NotNull DataResult<T> encode(final B input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return function.apply(input).flatMap(a -> Encoder.this.encode(a, ops, prefix));
            }

            @Override
            public String toString() {
                return Encoder.this + "[flatComapped]";
            }
        };
    }

    /**
     * Creates a new encoder that sets the lifecycle of the decoded result to
     * the given lifecycle.
     *
     * @param lifecycle The lifecycle.
     * @return A new encoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull Encoder<A> withLifecycle(final @NotNull Lifecycle lifecycle) {
        Objects.requireNonNull(lifecycle, "lifecycle");
        return new Encoder<>() {
            @Override
            public <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return Encoder.this.encode(input, ops, prefix).withLifecycle(lifecycle);
            }

            @Override
            public String toString() {
                return Encoder.this.toString();
            }
        };
    }
}
