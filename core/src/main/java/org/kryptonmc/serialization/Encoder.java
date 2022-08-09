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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.FieldEncoder;

@FunctionalInterface
public interface Encoder<A> {

    static <A> @NotNull MapEncoder<A> empty() {
        return new MapEncoder<>() {
            @Override
            public @NotNull <T> RecordBuilder<T> encode(@NotNull A input, @NotNull DataOps<T> ops, @NotNull RecordBuilder<T> prefix) {
                return prefix;
            }

            @Override
            public String toString() {
                return "EmptyEncoder";
            }
        };
    }

    <T> @NotNull T encode(@NotNull A input, @NotNull DataOps<T> ops, @NotNull T prefix);

    @ApiStatus.NonExtendable
    default <T> @NotNull T encodeStart(final @NotNull A input, final @NotNull DataOps<T> ops) {
        return encode(input, ops, ops.empty());
    }

    @ApiStatus.NonExtendable
    default @NotNull MapEncoder<A> field(final @NotNull String name) {
        return new FieldEncoder<>(name, this);
    }

    @ApiStatus.NonExtendable
    default <B> @NotNull Encoder<B> comap(final @NotNull Function<? super B, ? extends A> function) {
        return new Encoder<>() {
            @Override
            public <T> @NotNull T encode(final @NotNull B input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return Encoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public String toString() {
                return Encoder.this + "[comapped]";
            }
        };
    }
}
