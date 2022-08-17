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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/JsonOps.java
 */
package org.kryptonmc.serialization.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.DataResult;
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
    protected @NotNull JsonObject append(final @NotNull JsonObject builder, final @NotNull String key, final @NotNull JsonElement value) {
        builder.add(key, value);
        return builder;
    }

    @Override
    protected @NotNull DataResult<JsonElement> build(final @NotNull JsonObject builder, final @Nullable JsonElement prefix) {
        if (prefix == null || prefix instanceof JsonNull) return DataResult.success(builder);
        if (!(prefix instanceof final JsonObject object)) {
            return DataResult.error("Cannot merge map " + builder + " with a non-map " + prefix + " (attempting to build record builder)");
        }
        final var result = new JsonObject();
        for (final var entry : object.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        for (final var entry : builder.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        return DataResult.success(result);
    }
}
