/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.util.Either;

public record EitherCodec<L, R>(@NotNull Codec<L> left, @NotNull Codec<R> right) implements Codec<Either<L, R>> {

    @Override
    public <T> @NotNull Either<L, R> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        try {
            return Either.left(left.decode(input, ops));
        } catch (final Exception ignored) {
            return Either.right(right.decode(input, ops));
        }
    }

    @Override
    public <T> @NotNull T encode(final @NotNull Either<L, R> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return input.map(value -> left.encode(value, ops, prefix), value -> right.encode(value, ops, prefix));
    }

    @Override
    public String toString() {
        return "EitherCodec[" + left + ", " + right + ']';
    }
}
