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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/MapLike.java
 */
package org.kryptonmc.serialization;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.Pair;

/**
 * A type that represents a map-like structure for a generic data type.
 *
 * @param <T> The data type.
 */
public interface MapLike<T> {

    /**
     * Creates a wrapper object that delegates to the given map, using the
     * given ops for any conversions required between data types and standard
     * types.
     *
     * @param map The backing map.
     * @param ops The data operations.
     * @param <T> The data type.
     * @return A wrapper around the backing map.
     */
    static <T> @NotNull MapLike<T> forMap(final @NotNull Map<T, T> map, final @NotNull DataOps<T> ops) {
        Objects.requireNonNull(map, "map");
        Objects.requireNonNull(ops, "ops");
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

    /**
     * Gets the value for the given key, or returns null if there is no value
     * for the given key.
     *
     * @param key The key.
     * @return The value, or null if not present.
     */
    @Nullable T get(final @NotNull T key);

    /**
     * Gets the value for the given key, or returns null if there is no value
     * for the given key.
     *
     * @param key The key.
     * @return The value, or null if not present.
     */
    @Nullable T get(final @NotNull String key);

    /**
     * Gets the entries that are held by this map-like structure.
     *
     * @return The entries.
     */
    @NotNull Stream<Pair<T, T>> entries();
}
