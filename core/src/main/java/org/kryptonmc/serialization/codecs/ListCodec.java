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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/ListCodec.java
 */
package org.kryptonmc.serialization.codecs;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Lifecycle;
import org.kryptonmc.util.Pair;
import org.kryptonmc.util.Unit;

/**
 * A codec that will encode a list of values by encoding each value with the
 * given element codec, and decode an input to a list of values by decoding
 * each value with the given element codec.
 *
 * @param elementCodec The element codec.
 * @param <A> The element type.
 */
public record ListCodec<A>(@NotNull Codec<A> elementCodec) implements Codec<List<A>> {

    @SuppressWarnings("MissingJavadocMethod")
    public ListCodec {
        Objects.requireNonNull(elementCodec, "elementCodec");
    }

    @Override
    public <T> @NotNull DataResult<Pair<List<A>, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return ops.getList(input).withLifecycle(Lifecycle.stable()).flatMap(stream -> {
            final ImmutableList.Builder<A> read = ImmutableList.builder();
            final Stream.Builder<T> failed = Stream.builder();
            final AtomicReference<DataResult<Unit>> result = new AtomicReference<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                final var element = elementCodec.decode(t, ops);
                element.error().ifPresent(e -> failed.add(t));
                result.setPlain(result.getPlain().apply2stable((r, v) -> {
                    read.add(v.first());
                    return r;
                }, element));
            });

            final var elements = read.build();
            final var errors = ops.createList(failed.build());
            final Pair<List<A>, T> pair = Pair.of(elements, errors);
            return result.getPlain().map(unit -> pair).withPartial(pair);
        });
    }

    @Override
    public <T> @NotNull DataResult<T> encode(final @NotNull List<A> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        final var builder = ops.listBuilder();
        for (final var value : input) {
            builder.add(elementCodec.encodeStart(value, ops));
        }
        return builder.build(prefix);
    }

    @Override
    public String toString() {
        return "ListCodec[" + elementCodec + ']';
    }
}
