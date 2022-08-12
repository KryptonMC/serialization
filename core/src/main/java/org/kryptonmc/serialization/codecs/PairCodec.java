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
    public @NotNull <T> Pair<F, S> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return Pair.of(first.decode(input, ops), second.decode(input, ops));
    }

    @Override
    public <T> @NotNull T encode(final @NotNull Pair<F, S> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return first.encode(input.first(), ops, second.encode(input.second(), ops, prefix));
    }

    @Override
    public String toString() {
        return "PairCodec[" + first + ", " + second + ']';
    }
}
