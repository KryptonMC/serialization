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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/PairMapCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

/**
 * This is a map codec variant of {@link PairCodec}.
 *
 * @param first The first map codec.
 * @param second The second map codec.
 * @param <F> The first type.
 * @param <S> The second type.
 * @see PairCodec
 */
public record PairMapCodec<F, S>(@NotNull MapCodec<F> first, @NotNull MapCodec<S> second) implements MapCodec<Pair<F, S>> {

    @SuppressWarnings("MissingJavadocMethod")
    public PairMapCodec {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
    }

    @Override
    public <T> @NotNull DataResult<Pair<F, S>> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        return first.decode(input, ops).flatMap(p1 -> second.decode(input, ops).map(p2 -> Pair.of(p1, p2)));
    }

    @Override
    public <T> @NotNull RecordBuilder<T> encode(final @NotNull Pair<F, S> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        return first.encode(input.first(), ops, second.encode(input.second(), ops, prefix));
    }

    @Override
    public String toString() {
        return "PairMapCodec[" + first + ", " + second + ']';
    }
}
