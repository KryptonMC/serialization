/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.RecordBuilder;

final class JsonRecordBuilder extends RecordBuilder.AbstractStringBuilder<JsonElement, JsonObject> {

    JsonRecordBuilder() {
        super(GsonOps.INSTANCE);
    }

    @Override
    protected @NotNull JsonObject createBuilder() {
        return new JsonObject();
    }

    @Override
    protected void append(final @NotNull JsonObject builder, final @NotNull String key, final @NotNull JsonElement value) {
        builder.add(key, value);
    }

    @Override
    protected @NotNull JsonElement build(final @NotNull JsonObject builder, final @Nullable JsonElement prefix) {
        if (prefix == null || prefix instanceof JsonNull) return builder;
        if (!prefix.isJsonObject()) {
            throw new IllegalArgumentException("Cannot merge map " + builder + " with a non-map " + prefix + " (attempting to build record builder)");
        }
        final JsonObject result = new JsonObject();
        for (final var entry : prefix.getAsJsonObject().entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        for (final var entry : builder.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
