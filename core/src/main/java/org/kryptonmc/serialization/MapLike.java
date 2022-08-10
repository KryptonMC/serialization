/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.Pair;

public interface MapLike<T> {

    static <T> @NotNull MapLike<T> forMap(final @NotNull Map<T, T> map, final @NotNull DataOps<T> ops) {
        return new MapLike<>() {
            @Override
            public @Nullable T get(final @NotNull T key) {
                return map.get(key);
            }

            @Override
            public @Nullable T get(final @NotNull String key) {
                return get(ops.createString(key));
            }

            @Override
            public @NotNull Stream<Pair<T, T>> entries() {
                return map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
            }

            @Override
            public String toString() {
                return "MapLike[" + map + "]";
            }
        };
    }

    @Nullable T get(@NotNull T key);

    @Nullable T get(@NotNull String key);

    @NotNull Stream<Pair<T, T>> entries();
}
