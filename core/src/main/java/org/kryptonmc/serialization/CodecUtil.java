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
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

final class CodecUtil {

    public static MapEncoder<?> EMPTY = new MapEncoder<>() {
        @Override
        public <T> @NotNull RecordBuilder<T> encode(final Object input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
            return prefix;
        }

        @Override
        public String toString() {
            return "EmptyEncoder";
        }
    };

    public static <T> @NotNull UnaryOperator<T> consumerToFunction(final @NotNull Consumer<T> consumer) {
        return value -> {
            consumer.accept(value);
            return value;
        };
    }

    public static <N extends Number & Comparable<N>> @NotNull Function<N, DataResult<N>> checkRange(final @NotNull N minInclusive,
                                                                                                    final @NotNull N maxInclusive) {
        return value -> {
            if (value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0) return DataResult.success(value);
            return DataResult.error("Value " + value + " outside of range [" + minInclusive + ":" + maxInclusive + "]", value);
        };
    }

    private CodecUtil() {
    }
}
