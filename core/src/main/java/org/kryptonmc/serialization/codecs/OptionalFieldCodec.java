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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/OptionalFieldCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;

/**
 * A codec that will encode/decode a field with the given name to/from a map
 * optionally, returning {@link Optional#empty()} if the field is not present,
 * or the element codec throws an error when decoding.
 *
 * <p>This is an optimisation of
 * {@code Codec.either(elementCodec.field(name), Codec.EMPTY)}</p>
 *
 * @param name The field name.
 * @param elementCodec The field value codec.
 * @param <A> The field value type.
 */
public record OptionalFieldCodec<A>(@NotNull String name, @NotNull Codec<A> elementCodec) implements MapCodec<Optional<A>> {

    @Override
    public <T> @NotNull DataResult<Optional<A>> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var value = input.get(name);
        if (value == null) return DataResult.success(Optional.empty());
        final var parsed = elementCodec.read(value, ops);
        if (parsed.result().isPresent()) return parsed.map(Optional::of);
        return DataResult.success(Optional.empty());
    }

    @Override
    public <T> @NotNull RecordBuilder<T> encode(final @NotNull Optional<A> input, final @NotNull DataOps<T> ops,
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
