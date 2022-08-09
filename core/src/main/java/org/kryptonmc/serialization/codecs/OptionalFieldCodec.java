/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;

public record OptionalFieldCodec<A>(@NotNull String name, @NotNull Codec<A> elementCodec) implements MapCodec<Optional<A>> {

    @Override
    public @NotNull <T> Optional<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var value = input.get(name);
        if (value == null) return Optional.empty();
        try {
            return Optional.of(elementCodec.decode(value, ops));
        } catch (final Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull <T> RecordBuilder<T> encode(final @NotNull Optional<A> input, final @NotNull DataOps<T> ops,
                                                final @NotNull RecordBuilder<T> prefix) {
        // noinspection OptionalIsPresent
        if (input.isPresent()) return prefix.add(name, elementCodec.encodeStart(input.get(), ops));
        return prefix;
    }

    @Override
    public String toString() {
        return "OptionalFieldCodec[" + name + ": " + elementCodec + ']';
    }
}
