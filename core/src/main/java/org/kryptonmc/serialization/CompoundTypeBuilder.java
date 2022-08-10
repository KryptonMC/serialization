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

public sealed interface CompoundTypeBuilder<T> permits ListBuilder, RecordBuilder {

    @NotNull DataOps<T> ops();

    @NotNull T build(final @Nullable T prefix);
}
