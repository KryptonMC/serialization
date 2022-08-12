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

interface PrimitiveCodec<A> extends Codec<A> {

    <T> @NotNull A read(final @NotNull T input, final @NotNull DataOps<T> ops);

    <T> @NotNull T write(final @NotNull A value, final @NotNull DataOps<T> ops);

    @Override
    default <T> @NotNull A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return read(input, ops);
    }

    @Override
    default <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return ops.mergeToPrimitive(prefix, write(input, ops));
    }
}
