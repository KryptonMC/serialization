/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record Left<L, R>(L value) implements Either<L, R> {

    @Override
    public @NotNull Optional<L> left() {
        return Optional.of(value);
    }

    @Override
    public @NotNull Optional<R> right() {
        return Optional.empty();
    }

    @Override
    public @NotNull Either<L, R> ifLeft(final @NotNull Consumer<? super L> consumer) {
        consumer.accept(value);
        return this;
    }

    @Override
    public @NotNull Either<L, R> ifRight(final @NotNull Consumer<? super R> consumer) {
        return this;
    }

    @Override
    public <T> @NotNull T map(final @NotNull Function<? super L, ? extends T> leftMapper,
                              final @NotNull Function<? super R, ? extends T> rightMapper) {
        return leftMapper.apply(value);
    }

    @Override
    public <A, B> @NotNull Either<A, B> mapBoth(final @NotNull Function<? super L, ? extends A> leftMapper,
                                                final @NotNull Function<? super R, ? extends B> rightMapper) {
        return new Left<>(leftMapper.apply(value));
    }

    @Override
    public String toString() {
        return "Left[" + value + "]";
    }
}
