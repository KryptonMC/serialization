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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/ListBuilder.java
 */
package org.kryptonmc.serialization;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A builder that allows building a complex array-like structure for an
 * arbitrary data type, without having to know the details of the data type
 * being built.
 *
 * @param <T> The data type from the {@link DataOps} that created the builder.
 */
public non-sealed interface ListBuilder<T> extends CompoundTypeBuilder<T> {

    /**
     * Adds the given value to this builder.
     *
     * @param value The value to add.
     * @return This builder.
     */
    @Contract(value = "_ -> this", mutates = "this")
    @NotNull ListBuilder<T> add(final @NotNull T value);

    /**
     * Adds the given value to this builder.
     *
     * @param value The value to add.
     * @return This builder.
     */
    @Contract(value = "_ -> this", mutates = "this")
    @NotNull ListBuilder<T> add(final @NotNull DataResult<T> value);

    /**
     * Adds the given value to this builder, encoding it with the given encoder.
     *
     * @param value The value to add.
     * @param encoder The encoder to encode the value with.
     * @param <E> The value type.
     * @return This builder.
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    default <E> @NotNull ListBuilder<T> add(final @NotNull E value, final @NotNull Encoder<E> encoder) {
        return add(encoder.encodeStart(value, ops()));
    }

    /**
     * Adds all the given values to this builder, encoding each individual
     * value with the given encoder.
     *
     * @param values The values to add.
     * @param encoder The encoder to encode the values with.
     * @param <E> The value type.
     * @return This builder.
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    default <E> @NotNull ListBuilder<T> addAll(final @NotNull Iterable<E> values, final @NotNull Encoder<E> encoder) {
        values.forEach(value -> encoder.encode(value, ops(), ops().empty()));
        return this;
    }

    /**
     * Adds all the errors, if any, from the given result to the result being
     * built by this builder.
     *
     * @param result The result to add errors from.
     * @return This builder.
     */
    @Contract(value = "_ -> this", mutates = "this")
    @NotNull ListBuilder<T> withErrorsFrom(final @NotNull DataResult<?> result);

    /**
     * Maps the error message of this builder, if any, with the given on error
     * mapper.
     *
     * @param onError The on error mapper.
     * @return This builder.
     */
    @Contract(value = "_ -> this", mutates = "this")
    @NotNull ListBuilder<T> mapError(final @NotNull UnaryOperator<String> onError);

    /**
     * Builds the complex type.
     *
     * @param prefix The prefix to prepend to the result.
     * @return The built result.
     */
    default @NotNull DataResult<T> build(final @NotNull DataResult<T> prefix) {
        return prefix.flatMap(this::build);
    }

    /**
     * The default implementation of {@link ListBuilder} that adds the values
     * to an intermediary {@link ArrayList} as its builder type and merges the
     * intermediary list with the prefix using {@link DataOps#mergeToList(Object, List)}}.
     *
     * @param <T> The data type.
     */
    final class Default<T> implements ListBuilder<T> {

        private final DataOps<T> ops;
        private DataResult<ImmutableList.Builder<T>> builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());

        /**
         * Creates a new default builder with the given ops.
         *
         * @param ops The data operations.
         */
        public Default(final @NotNull DataOps<T> ops) {
            this.ops = ops;
        }

        @Override
        public @NotNull DataOps<T> ops() {
            return ops;
        }

        @Override
        public @NotNull ListBuilder<T> add(final @NotNull T value) {
            builder = builder.map(b -> b.add(value));
            return this;
        }

        @Override
        public @NotNull ListBuilder<T> add(final @NotNull DataResult<T> value) {
            builder = builder.apply2stable(ImmutableList.Builder::add, value);
            return this;
        }

        @Override
        public @NotNull ListBuilder<T> withErrorsFrom(final @NotNull DataResult<?> result) {
            builder = builder.flatMap(r -> result.map(v -> r));
            return this;
        }

        @Override
        public @NotNull ListBuilder<T> mapError(final @NotNull UnaryOperator<String> onError) {
            builder = builder.mapError(onError);
            return this;
        }

        @Override
        public @NotNull DataResult<T> build(final T prefix) {
            final var result = builder.flatMap(b -> ops.mergeToList(prefix, b.build()));
            builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());
            return result;
        }
    }
}
