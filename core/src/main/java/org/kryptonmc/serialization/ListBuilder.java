/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.ArrayList;
import java.util.List;
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
     * The default implementation of {@link ListBuilder} that adds the values
     * to an intermediary {@link ArrayList} as its builder type and merges the
     * intermediary list with the prefix using {@link DataOps#mergeToList(Object, List)}}.
     *
     * @param <T> The data type.
     */
    final class Default<T> implements ListBuilder<T> {

        private final DataOps<T> ops;
        private final List<T> result = new ArrayList<>();

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
            result.add(value);
            return this;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public @NotNull T build(final @NotNull T prefix) {
            return ops.mergeToList(prefix, result);
        }
    }
}
