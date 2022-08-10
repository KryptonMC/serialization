package org.kryptonmc.serialization.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.nbt.CompoundTag;
import org.kryptonmc.nbt.EndTag;
import org.kryptonmc.nbt.Tag;
import org.kryptonmc.serialization.RecordBuilder;

final class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag.Builder> {

    public NbtRecordBuilder() {
        super(NbtOps.INSTANCE);
    }

    @Override
    protected @NotNull CompoundTag.Builder createBuilder() {
        return CompoundTag.immutableBuilder();
    }

    @Override
    protected void append(final CompoundTag.@NotNull Builder builder, final @NotNull String key, final @NotNull Tag value) {
        builder.put(key, value);
    }

    @Override
    protected @NotNull Tag build(final CompoundTag.@NotNull Builder builder, final @Nullable Tag prefix) {
        if (prefix == null || prefix == EndTag.INSTANCE) return builder.build();
        if (!(prefix instanceof final CompoundTag tag)) {
            throw new IllegalArgumentException("Cannot merge map " + builder + " with a non-map " + prefix + " (attempting to build record builder)");
        }
        return tag.toBuilder().from(builder).build();
    }
}
