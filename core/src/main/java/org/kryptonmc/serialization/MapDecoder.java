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
public interface MapDecoder<A> {

    <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops);

    default <T> A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return decode(ops.getMap(input), ops);
    }

    default <B> @NotNull MapDecoder<B> map(final @NotNull Function<? super A, ? extends B> function) {
        return new MapDecoder<>() {
            @Override
            public <T> B decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return function.apply(MapDecoder.this.decode(input, ops));
            }

            @Override
            public String toString() {
                return MapDecoder.this + "[mapped]";
            }
        };
    }
}
