/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MapEncoder<A> {

    <T> @NotNull RecordBuilder<T> encode(@NotNull A input, @NotNull DataOps<T> ops, @NotNull RecordBuilder<T> prefix);

    default <B> @NotNull MapEncoder<B> comap(final @NotNull Function<? super B, ? extends A> function) {
        return new MapEncoder<B>() {
            @Override
            public @NotNull <T> RecordBuilder<T> encode(final @NotNull B input, final @NotNull DataOps<T> ops,
                                                        final @NotNull RecordBuilder<T> prefix) {
                return MapEncoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public String toString() {
                return MapEncoder.this + "[comapped]";
            }
        };
    }

    default @NotNull Encoder<A> encoder() {
        return new Encoder<A>() {
            @Override
            public <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return MapEncoder.this.encode(input, ops, ops.mapBuilder()).build(prefix);
            }

            @Override
            public String toString() {
                return MapEncoder.this.toString();
            }
        };
    }
}
