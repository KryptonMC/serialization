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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/EitherMapCodec.java
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Either;

/**
 * This is a map codec variant of {@link EitherCodec}.
 *
 * @param left The left map codec.
 * @param right The right map codec.
 * @param <L> The left type.
 * @param <R> The right type.
 * @see EitherCodec
 */
public record EitherMapCodec<L, R>(@NotNull MapCodec<L> left, @NotNull MapCodec<R> right) implements MapCodec<Either<L, R>> {

    @Override
    public @NotNull <T> Either<L, R> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        try {
            return Either.left(left.decode(input, ops));
        } catch (final Exception ignored) {
            return Either.right(right.decode(input, ops));
        }
    }

    @Override
    public @NotNull <T> RecordBuilder<T> encode(final @NotNull Either<L, R> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        return input.map(value -> left.encode(value, ops, prefix), value -> right.encode(value, ops, prefix));
    }

    @Override
    public String toString() {
        return "EitherMapCodec[" + left + ", " + right + ']';
    }
}
