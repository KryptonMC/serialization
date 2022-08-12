/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A builder for a compound type. This is a common abstraction over the complex
 * type builders {@link ListBuilder} and {@link RecordBuilder}, which are for
 * building generic lists and map-like structures respectively.
 *
 * @param <T> The compound type. This will be the same as the generic type of
 *            the {@link DataOps} that created it.
 */
public sealed interface CompoundTypeBuilder<T> permits ListBuilder, RecordBuilder {

    /**
     * Gets the operations that created this builder, which can be used by the
     * builder implementations to convert data.
     *
     * @return the operations that created this builder
     */
    @NotNull DataOps<T> ops();

    /**
     * Builds the complex type.
     *
     * @param prefix The prefix to prepend to the result.
     * @return The built result.
     * @implSpec This method should always try to use the prefix. If the prefix
     *           is {@code null}, it should be ignored, and the result should
     *           be returned as-is as if the prefix wasn't a factor at all. If
     *           the prefix is given, it should always be prepended (placed
     *           before) the result, unless the format is not ordered, in which
     *           it does not matter where the prefix is placed, as long as it
     *           is in the result.
     */
    @NotNull T build(final @Nullable T prefix);
}
