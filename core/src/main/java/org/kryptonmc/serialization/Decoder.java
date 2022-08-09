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
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.FieldDecoder;

@FunctionalInterface
public interface Decoder<A> {

    static <A> @NotNull MapDecoder<A> unit(final @NotNull A instance) {
        return unit(() -> instance);
    }

    static <A> @NotNull MapDecoder<A> unit(final @NotNull Supplier<A> instance) {
        return new MapDecoder<>() {
            @Override
            public <T> @NotNull A decode(@NotNull MapLike<T> input, @NotNull DataOps<T> ops) {
                return instance.get();
            }

            @Override
            public String toString() {
                return "UnitDecoder[" + instance.get() + "]";
            }
        };
    }

    <T> @NotNull A decode(final @NotNull T input, final @NotNull DataOps<T> ops);

    @ApiStatus.NonExtendable
    default @NotNull MapDecoder<A> field(final @NotNull String name) {
        return new FieldDecoder<>(name, this);
    }

    @ApiStatus.NonExtendable
    default <B> @NotNull Decoder<B> map(final @NotNull Function<? super A, ? extends B> function) {
        return new Decoder<>() {
            @Override
            public <T> @NotNull B decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
                return function.apply(Decoder.this.decode(input, ops));
            }

            @Override
            public String toString() {
                return Decoder.this + "[mapped]";
            }
        };
    }
}
