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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.FieldDecoder;
import org.kryptonmc.util.Pair;

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
        Objects.requireNonNull(instance, "instance");
        return new MapDecoder<>() {

            private final DataResult<A> result = DataResult.success(instance);

            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return result;
            }

            @Override
            public String toString() {
                return "UnitDecoder[" + instance + "]";
            }
        };
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
        Objects.requireNonNull(instance, "instance");
        return new MapDecoder<>() {
            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return DataResult.success(instance.get());
            }

            @Override
            public String toString() {
                return "UnitDecoder[" + instance.get() + "]";
            }
        };
    }

    /**
     * Creates a new decoder that always returns a result with the given error
     * message.
     *
     * @param error The error message.
     * @param <A> The value type.
     * @return A new error decoder.
     */
    static <A> @NotNull Decoder<A> error(final @NotNull String error) {
        Objects.requireNonNull(error, "error");
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return DataResult.error(error);
            }

            @Override
            public String toString() {
                return "ErrorDecoder[" + error + ']';
            }
        };
    }

    /**
     * Decodes the given input data to the standard type that this decoder is
     * for, using the given operations to convert the input in to standard
     * types that can be used generically when decoding.
     *
     * <p>This method returns a pair of values as its result. The first is the
     * decoded result, and the second is any partial result that may be
     * present.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The decoded result.
     */
    <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops);

    /**
     * Decodes the given input data to the standard type that this decoder is
     * for, using the given operations to convert the input in to standard
     * types that can be used generically when decoding.
     *
     * <p>This method takes the result from {@link #decode(Object, DataOps)}
     * and disregards any possible partial result that may be returned.</p>
     *
     * @param input The input.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return The decoded result.
     */
    @ApiStatus.NonExtendable
    default <T> @NotNull DataResult<A> read(final T input, final @NotNull DataOps<T> ops) {
        return decode(input, ops).map(Pair::first);
    }

    /**
     * Decodes the given dynamic data input to the standard type that this
     * decoder is for.
     *
     * @param input The input.
     * @param <T> The data type.
     * @return The decoded result.
     */
    @ApiStatus.NonExtendable
    default <T> @NotNull DataResult<Pair<A, T>> decode(final @NotNull Dynamic<T> input) {
        return decode(input.value(), input.ops());
    }

    /**
     * Decodes the given dynamic data input to the standard type that this
     * decoder is for.
     *
     * <p>This method takes the result from {@link #decode(Dynamic)} and
     * disregards any possible partial result that may be returned.</p>
     *
     * @param input The input.
     * @param <T> The data type.
     * @return The decoded result.
     */
    @ApiStatus.NonExtendable
    default <T> @NotNull DataResult<A> read(final @NotNull Dynamic<T> input) {
        return decode(input).map(Pair::first);
    }

    /**
     * Creates a new decoder that decodes a field with the given name using
     * this decoder to decode the value of the field.
     *
     * @param name The name of the field.
     * @return A new field decoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapDecoder<A> fieldOf(final @NotNull String name) {
        return new FieldDecoder<>(name, this);
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
    default <B> @NotNull Decoder<B> map(final @NotNull Function<? super A, ? extends B> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<B, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return Decoder.this.decode(input, ops).map(result -> result.mapFirst(mapper));
            }

            @Override
            public String toString() {
                return Decoder.this + "[mapped]";
            }
        };
    }

    /**
     * Maps this decoder to a new decoder, using the given function to map
     * results from this decoder to a new type for the new decoder.
     *
     * <p>This method is different to {@link #map(Function)} in that the
     * function returns a {@link DataResult}.</p>
     *
     * @param mapper The function to apply when mapping the decoded value from
     *               this decoder.
     * @param <B> The new value type.
     * @return The resulting mapped decoder.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Decoder<B> flatMap(final @NotNull Function<? super A, ? extends DataResult<? extends B>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<B, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return Decoder.this.decode(input, ops).flatMap(result -> mapper.apply(result.first()).map(r -> Pair.of(r, result.second())));
            }

            @Override
            public String toString() {
                return Decoder.this + "[flatMapped]";
            }
        };
    }

    /**
     * Promotes the partial result of the decoded result, if any, calling the
     * given onError function if an error is present in the partial result.
     *
     * @param onError The on error function.
     * @return The resulting decoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull Decoder<A> promotePartial(final @NotNull Consumer<String> onError) {
        Objects.requireNonNull(onError, "onError");
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return Decoder.this.decode(input, ops).promotePartial(onError);
            }

            @Override
            public String toString() {
                return Decoder.this + "[promotePartial]";
            }
        };
    }

    /**
     * Creates a new decoder that sets the lifecycle of the decoded result to
     * the given lifecycle.
     *
     * @param lifecycle The lifecycle.
     * @return A new decoder.
     */
    @ApiStatus.NonExtendable
    default @NotNull Decoder<A> withLifecycle(final @NotNull Lifecycle lifecycle) {
        Objects.requireNonNull(lifecycle, "lifecycle");
        return new Decoder<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return Decoder.this.decode(input, ops).withLifecycle(lifecycle);
            }

            @Override
            public String toString() {
                return Decoder.this.toString();
            }
        };
    }
}
