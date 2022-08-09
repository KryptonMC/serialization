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

public non-sealed interface ListBuilder<T> extends CompoundTypeBuilder<T> {

    @Contract(value = "_ -> this", mutates = "this")
    @NotNull ListBuilder<T> add(@NotNull T value);

    @Contract(value = "_, _ -> this", mutates = "this")
    default <E> @NotNull ListBuilder<T> add(final @NotNull E value, final @NotNull Encoder<E> encoder) {
        return add(encoder.encodeStart(value, ops()));
    }

    @Contract(value = "_, _ -> this", mutates = "this")
    default <E> @NotNull ListBuilder<T> addAll(final @NotNull Iterable<E> values, final @NotNull Encoder<E> encoder) {
        values.forEach(value -> encoder.encode(value, ops(), ops().empty()));
        return this;
    }

    final class Default<T> implements ListBuilder<T> {

        private final DataOps<T> ops;
        private final List<T> result = new ArrayList<>();

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

        @Override
        public @NotNull T build(final @NotNull T prefix) {
            return ops.mergeToList(prefix, result);
        }
    }
}
