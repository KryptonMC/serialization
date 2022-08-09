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
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

public record PairMapCodec<F, S>(@NotNull MapCodec<F> first, @NotNull MapCodec<S> second) implements MapCodec<Pair<F, S>> {

    @Override
    public @NotNull <T> Pair<F, S> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        return Pair.of(first.decode(input, ops), second.decode(input, ops));
    }

    @Override
    public @NotNull <T> RecordBuilder<T> encode(final @NotNull Pair<F, S> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        return first.encode(input.first(), ops, second.encode(input.second(), ops, prefix));
    }

    @Override
    public String toString() {
        return "PairMapCodec[" + first + ", " + second + ']';
    }
}
