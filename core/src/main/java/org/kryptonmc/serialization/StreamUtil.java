/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

final class StreamUtil {

    public static <T> boolean noneThrow(final @NotNull Stream<T> stream, final @NotNull Consumer<T> consumer) {
        return stream.allMatch(element -> {
            try {
                consumer.accept(element);
                return true;
            } catch (final Exception ignored) {
                return false;
            }
        });
    }

    public static <T> @NotNull Stream<T> orEmpty(final @NotNull Supplier<Stream<T>> streamSupplier) {
        try {
            return streamSupplier.get();
        } catch (final Exception ignored) {
            return Stream.empty();
        }
    }
}
