/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A builder that allows building a complex map-like structure for an arbitrary
 * data type, without having to know the details of the data type being built.
 *
 * @param <T> The data type from the {@link DataOps} that created the builder.
 */
public non-sealed interface RecordBuilder<T> extends CompoundTypeBuilder<T> {

    /**
     * Adds the given key and value to the record type being built by this
     * builder, mapping the key to the value.
     *
     * @param key The key.
     * @param value The value.
     * @return This builder.
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    @NotNull RecordBuilder<T> add(final @NotNull T key, final @NotNull T value);

    /**
     * Adds the given key and value to the record type being built by this
     * builder, mapping the key to the value.
     *
     * @param key The key.
     * @param value The value.
     * @return This builder.
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    default @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull T value) {
        return add(ops().createString(key), value);
    }

    /**
     * Adds the given key and value to the record type being built by this
     * builder, encoding the result with the given encoder and mapping the
     * result to the key.
     *
     * @param key The key.
     * @param value The value.
     * @param encoder The encoder used to encode the value.
     * @param <E> The type of the value being encoded.
     * @return This builder.
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    default <E> @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull E value, final @NotNull Encoder<E> encoder) {
        return add(key, encoder.encodeStart(value, ops()));
    }

    /**
     * An abstract implementation of {@link RecordBuilder} that takes the ops
     * as a parameter and provides the implementation for the {@link #ops()}
     * method. This builder also stores a builder and provides a default
     * implementation of {@link #build(T)} by delegating to an abstract builder
     * that provides implementations with the builder being used to store the
     * intermediary mappings.
     *
     * @param <T> The type of the record being built.
     * @param <B> The builder type.
     */
    abstract class AbstractBuilder<T, B> implements RecordBuilder<T> {

        private final DataOps<T> ops;
        protected final B builder = createBuilder();

        protected AbstractBuilder(final @NotNull DataOps<T> ops) {
            this.ops = ops;
        }

        /**
         * Creates the initial backing builder that will be appended to by this
         * builder's {@code add} methods.
         *
         * @return The initial backing builder.
         */
        protected abstract @NotNull B createBuilder();

        /**
         * Creates the data type being built by this builder from the given
         * builder and prefix if present.
         *
         * @param builder The builder to create the data type from.
         * @param prefix The prefix to prepend before the builder's data.
         * @return The built object.
         * @implNote If present, the prefix must always be prepended (added
         *           before) the data, never after, except in the case where,
         *           in the specific data type, the specific ordering of data
         *           does not matter, in which it can be added after if it is
         *           the only possible way to construct the data type.
         */
        protected abstract @NotNull T build(final @NotNull B builder, final @Nullable T prefix);

        @Override
        public @NotNull DataOps<T> ops() {
            return ops;
        }

        @Override
        public @NotNull T build(final @Nullable T prefix) {
            return build(builder, prefix);
        }
    }

    /**
     * An abstract implementation of {@link RecordBuilder} that inverts the
     * {@link #add(T, T)} and {@link #add(String, T)} methods to make it easier
     * to implement data types with string keys. This builder has an
     * {@link #append(B, String, T)} method that implementations need to
     * override to specify how to append the key and value to the builder.
     *
     * @param <T> The type of the record being built.
     * @param <B> The builder type.
     */
    abstract class AbstractStringBuilder<T, B> extends AbstractBuilder<T, B> {

        protected AbstractStringBuilder(final @NotNull DataOps<T> ops) {
            super(ops);
        }

        /**
         * Appends the given key and value to the given builder.
         *
         * @param builder The builder to append to.
         * @param key The key to append.
         * @param value The value to append.
         */
        protected abstract void append(final @NotNull B builder, final @NotNull String key, final @NotNull T value);

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull T key, final @NotNull T value) {
            add(ops().getStringValue(key), value);
            return this;
        }

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull T value) {
            append(builder, key, value);
            return this;
        }
    }

    /**
     * An abstract implementation of {@link RecordBuilder} that allows
     * implementations to easily implement {@link RecordBuilder} for types that
     * have {@link T}-based keys.
     *
     * @param <T> The type of the record being built.
     * @param <B> The builder type.
     */
    abstract class AbstractUniversalBuilder<T, B> extends AbstractBuilder<T, B> {

        protected AbstractUniversalBuilder(final @NotNull DataOps<T> ops) {
            super(ops);
        }

        /**
         * Appends the given key and value to the given builder.
         *
         * @param builder The builder to append to.
         * @param key The key to append.
         * @param value The value to append.
         */
        protected abstract void append(final @NotNull B builder, final @NotNull T key, final @NotNull T value);

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull T key, final @NotNull T value) {
            append(builder, key, value);
            return this;
        }
    }

    /**
     * The default implementation of {@link RecordBuilder} that adds the
     * entries to an intermediary {@link HashMap} as its builder type and
     * merges the intermediary map with the prefix using
     * {@link DataOps#mergeToMap(Object, Map)}.
     *
     * @param <T> The type of the record being built.
     */
    final class Default<T> extends AbstractUniversalBuilder<T, Map<T, T>> {

        /**
         * Creates a new default record builder with the given ops.
         *
         * @param ops The data operations.
         */
        public Default(final @NotNull DataOps<T> ops) {
            super(ops);
        }

        @Override
        protected @NotNull Map<T, T> createBuilder() {
            return new HashMap<>();
        }

        @Override
        protected void append(final @NotNull Map<T, T> builder, final @NotNull T key, final @NotNull T value) {
            builder.put(key, value);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        protected @NotNull T build(final @NotNull Map<T, T> builder, final @NotNull T prefix) {
            return ops().mergeToMap(prefix, builder);
        }
    }
}
