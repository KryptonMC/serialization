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
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Lifecycle;
import org.kryptonmc.serialization.ListBuilder;

final class ArrayBuilder implements ListBuilder<JsonElement> {

    private DataResult<JsonArray> builder = DataResult.success(new JsonArray(), Lifecycle.stable());

    @Override
    public @NotNull DataOps<JsonElement> ops() {
        return GsonOps.INSTANCE;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> add(final @NotNull JsonElement value) {
        builder = builder.map(b -> {
            b.add(value);
            return b;
        });
        return this;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> add(final @NotNull DataResult<JsonElement> value) {
        builder = builder.apply2stable((b, element) -> {
            b.add(element);
            return b;
        }, value);
        return this;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> withErrorsFrom(final @NotNull DataResult<?> result) {
        builder = builder.flatMap(r -> result.map(v -> r));
        return this;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> mapError(final @NotNull UnaryOperator<String> onError) {
        builder = builder.mapError(onError);
        return this;
    }

    @Override
    public @NotNull DataResult<JsonElement> build(final @Nullable JsonElement prefix) {
        final DataResult<JsonElement> result = builder.flatMap(b -> {
            if (!(prefix instanceof JsonArray) && prefix != ops().empty()) return DataResult.error("Cannot append a list to a non-list: " + prefix);
            final var array = new JsonArray();
            if (prefix != ops().empty()) array.addAll(prefix.getAsJsonArray());
            array.addAll(b);
            return DataResult.success(array, Lifecycle.stable());
        });
        builder = DataResult.success(new JsonArray(), Lifecycle.stable());
        return result;
    }
}
