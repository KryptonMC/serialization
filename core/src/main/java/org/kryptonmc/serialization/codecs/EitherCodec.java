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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/EitherCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.util.Either;
import org.kryptonmc.util.Pair;

/**
 * A codec that will encode/decode the value with the left codec if it can, or
 * fallback to encoding/decoding the value with the right codec if the left
 * codec fails.
 *
 * <p>For decoding, this will try to decode the left value with the left codec,
 * and if an exception occurs, it will decode the value with the right codec.
 * This will, however, not swallow exceptions from the right codec. If it fails
 * to decode the value, the error will be propagated up the stack as normal.</p>
 *
 * <p>For encoding, this will map the input, encoding with the left codec if
 * the value is left, and encoding with the right codec if the value is right.
 * This will always propagate encoding errors up the stack.</p>
 *
 * @param left The left codec.
 * @param right The right codec.
 * @param <L> The left type.
 * @param <R> The right type.
 */
public record EitherCodec<L, R>(@NotNull Codec<L> left, @NotNull Codec<R> right) implements Codec<Either<L, R>> {

    @SuppressWarnings("MissingJavadocMethod")
    public EitherCodec {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
    }

    @Override
    public <T> @NotNull DataResult<Pair<Either<L, R>, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        final DataResult<Pair<Either<L, R>, T>> leftRead = left.decode(input, ops).map(vo -> vo.mapFirst(Either::left));
        if (leftRead.result().isPresent()) return leftRead;
        return right.decode(input, ops).map(vo -> vo.mapFirst(Either::right));
    }

    @Override
    public <T> @NotNull DataResult<T> encode(final @NotNull Either<L, R> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return input.map(value -> left.encode(value, ops, prefix), value -> right.encode(value, ops, prefix));
    }

    @Override
    public String toString() {
        return "EitherCodec[" + left + ", " + right + ']';
    }
}
