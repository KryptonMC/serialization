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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/PrimitiveCodec.java
 */
package org.kryptonmc.serialization;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

interface PrimitiveCodec<A> extends Codec<A> {

    @Override
    <T> @NotNull DataResult<A> read(final @NotNull T input, final @NotNull DataOps<T> ops);

    <T> @NotNull T write(final @NotNull A value, final @NotNull DataOps<T> ops);

    @Override
    default <T> @NotNull DataResult<Pair<A, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
        return read(input, ops).map(result -> Pair.of(result, ops.empty()));
    }

    @Override
    default <T> @NotNull DataResult<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
        return ops.mergeToPrimitive(prefix, write(input, ops));
    }
}
