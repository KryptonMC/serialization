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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.ListBuilder;

final class ArrayBuilder implements ListBuilder<JsonElement> {

    private final JsonArray result = new JsonArray();

    @Override
    public @NotNull DataOps<JsonElement> ops() {
        return GsonOps.INSTANCE;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> add(final @NotNull JsonElement value) {
        result.add(value);
        return this;
    }

    @Override
    public @NotNull JsonElement build(final @Nullable JsonElement prefix) {
        if (prefix == null) return result;
        if (!prefix.isJsonArray() && prefix != ops().empty()) {
            throw new IllegalStateException("Cannot append a list to a non-list: " + prefix);
        }
        final JsonArray array = new JsonArray();
        if (prefix != ops().empty()) array.addAll(prefix.getAsJsonArray());
        array.addAll(result);
        return array;
    }
}
