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

public non-sealed interface RecordBuilder<T> extends CompoundTypeBuilder<T> {

    @Contract(value = "_, _ -> this", mutates = "this")
    @NotNull RecordBuilder<T> add(@NotNull T key, @NotNull T value);

    @Contract(value = "_, _ -> this", mutates = "this")
    default @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull T value) {
        return add(ops().createString(key), value);
    }

    @Contract(value = "_, _, _ -> this", mutates = "this")
    default <E> @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull E value, final @NotNull Encoder<E> encoder) {
        return add(key, encoder.encodeStart(value, ops()));
    }

    abstract class AbstractBuilder<T, R> implements RecordBuilder<T> {

        private final DataOps<T> ops;
        protected final R builder = createBuilder();

        protected AbstractBuilder(final @NotNull DataOps<T> ops) {
            this.ops = ops;
        }

        protected abstract @NotNull R createBuilder();

        protected abstract @NotNull T build(final @NotNull R builder, final @Nullable T prefix);

        @Override
        public @NotNull DataOps<T> ops() {
            return ops;
        }

        @Override
        public @NotNull T build(final @Nullable T prefix) {
            return build(builder, prefix);
        }
    }

    abstract class AbstractStringBuilder<T, R> extends AbstractBuilder<T, R> {

        protected AbstractStringBuilder(final @NotNull DataOps<T> ops) {
            super(ops);
        }

        protected abstract void append(final @NotNull R builder, final @NotNull String key, final @NotNull T value);

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull T key, final @NotNull T value) {
            try {
                add(ops().getStringValue(key), value);
            } catch (final Exception ignored) {
            }
            return this;
        }

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull String key, final @NotNull T value) {
            append(builder, key, value);
            return this;
        }
    }

    abstract class AbstractUniversalBuilder<T, R> extends AbstractBuilder<T, R> {

        protected AbstractUniversalBuilder(final @NotNull DataOps<T> ops) {
            super(ops);
        }

        protected abstract void append(final @NotNull R builder, final @NotNull T key, final @NotNull T value);

        @Override
        public @NotNull RecordBuilder<T> add(final @NotNull T key, final @NotNull T value) {
            append(builder, key, value);
            return this;
        }
    }

    final class Default<T> extends AbstractUniversalBuilder<T, Map<T, T>> {

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
