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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/PairCodec.java
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.util.Pair;

/**
 * A codec that encodes/decodes a pair of values.
 *
 * @param first The first codec.
 * @param second The second value.
 * @param <F> The first type.
 * @param <S> The second type.
 */
public record PairCodec<F, S>(@NotNull Codec<F> first, @NotNull Codec<S> second) implements Codec<Pair<F, S>> {

    @Override
    public <T> @NotNull DataResult<Pair<Pair<F, S>, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return first.decode(input, ops)
                .flatMap(p1 -> second.decode(p1.second(), ops).map(p2 -> Pair.of(Pair.of(p1.first(), p2.first()), p2.second())));
    }

    @Override
    public <T> @NotNull DataResult<T> encode(final @NotNull Pair<F, S> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return second.encode(input.second(), ops, prefix).flatMap(f -> first.encode(input.first(), ops, f));
    }

    @Override
    public String toString() {
        return "PairCodec[" + first + ", " + second + ']';
    }
}
