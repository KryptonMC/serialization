/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.nbt.CompoundTag;
import org.kryptonmc.nbt.EndTag;
import org.kryptonmc.nbt.ImmutableCompoundTag;
import org.kryptonmc.nbt.Tag;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.RecordBuilder;

final class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag.Builder> {

    NbtRecordBuilder() {
        super(NbtOps.INSTANCE);
    }

    @Override
    protected @NotNull CompoundTag.Builder createBuilder() {
        return ImmutableCompoundTag.builder();
    }

    @Override
    protected @NotNull CompoundTag.Builder append(final CompoundTag.@NotNull Builder builder, final @NotNull String key, final @NotNull Tag value) {
        return builder.put(key, value);
    }

    @Override
    protected @NotNull DataResult<Tag> build(final CompoundTag.@NotNull Builder builder, final @Nullable Tag prefix) {
        if (prefix == null || prefix == EndTag.INSTANCE) return DataResult.success(builder.build());
        if (!(prefix instanceof final CompoundTag tag)) {
            return DataResult.error("Cannot merge map " + builder + " with a non-map " + prefix + "! (attempting to build record builder)");
        }
        return DataResult.success(tag.toBuilder().from(builder).build());
    }
}
