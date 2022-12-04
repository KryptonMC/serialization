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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.ListBuilder;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

/**
 * The standard data operations for the Gson JSON library.
 */
public final class GsonOps implements DataOps<JsonElement> {

    public static final @NotNull GsonOps INSTANCE = new GsonOps();

    private GsonOps() {
    }

    @Override
    public @NotNull JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public @NotNull DataResult<Boolean> getBooleanValue(final @NotNull JsonElement input) {
        if (input instanceof final JsonPrimitive primitive) {
            if (primitive.isBoolean()) return DataResult.success(input.getAsBoolean());
            if (primitive.isNumber()) return DataResult.success(input.getAsNumber().byteValue() != 0);
        }
        return error("getBooleanValue", "boolean", input);
    }

    @Override
    public @NotNull DataResult<Number> getNumberValue(final @NotNull JsonElement input) {
        if (input instanceof final JsonPrimitive primitive) {
            if (primitive.isNumber()) return DataResult.success(input.getAsNumber());
            if (primitive.isBoolean()) return DataResult.success(input.getAsBoolean() ? 1 : 0);
        }
        return error("getNumberValue", "number", input);
    }

    @Override
    public @NotNull DataResult<String> getStringValue(final @NotNull JsonElement input) {
        if (input instanceof final JsonPrimitive primitive && primitive.isString()) return DataResult.success(input.getAsString());
        return error("getStringValue", "string", input);
    }

    @Override
    public @NotNull DataResult<Stream<JsonElement>> getStream(final @NotNull JsonElement input) {
        if (input instanceof final JsonArray array) return DataResult.success(StreamSupport.stream(array.spliterator(), false).map(GsonOps::orNull));
        return error("getStream", "json array", input);
    }

    @Override
    public @NotNull DataResult<Consumer<Consumer<JsonElement>>> getList(final @NotNull JsonElement input) {
        if (input instanceof final JsonArray array) {
            return DataResult.success(consumer -> {
                for (final var element : array) {
                    consumer.accept(orNull(element));
                }
            });
        }
        return error("getList", "json array", input);
    }

    @Override
    public @NotNull DataResult<JsonElement> mergeToList(final @NotNull JsonElement list, final @NotNull JsonElement value) {
        if (!list.isJsonArray() && list != empty()) return DataResult.error("Cannot merge value " + value + " in to non-list " + list + "!");
        final var result = new JsonArray();
        if (list != empty()) result.addAll(list.getAsJsonArray());
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<JsonElement> mergeToList(final @NotNull JsonElement list, final @NotNull List<JsonElement> values) {
        if (!list.isJsonArray() && list != empty()) return DataResult.error("Cannot merge values " + values + " in to non-list " + list + "!");
        final var result = new JsonArray();
        if (list != empty()) result.addAll(list.getAsJsonArray());
        values.forEach(result::add);
        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(final @NotNull JsonElement input) {
        if (!(input instanceof final JsonObject object)) return error("getMapValues", "json object", input);
        return DataResult.success(object.entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), orNull(entry.getValue()))));
    }

    @Override
    public @NotNull DataResult<Consumer<BiConsumer<JsonElement, JsonElement>>> getMapEntries(final @NotNull JsonElement input) {
        if (!(input instanceof final JsonObject object)) return error("getMapEntries", "json object", input);
        return DataResult.success(consumer -> {
            for (final var entry : object.entrySet()) {
                consumer.accept(createString(entry.getKey()), orNull(entry.getValue()));
            }
        });
    }

    @Override
    public @NotNull DataResult<MapLike<JsonElement>> getMap(final @NotNull JsonElement input) {
        if (!(input instanceof final JsonObject object)) return error("getMap", "json object", input);
        return DataResult.success(new MapLike<>() {
            @Override
            public @Nullable JsonElement get(final @NotNull JsonElement key) {
                return orNull(object.get(key.getAsString()));
            }

            @Override
            public @Nullable JsonElement get(final @NotNull String key) {
                return orNull(object.get(key));
            }

            @Override
            public @NotNull Stream<Pair<JsonElement, JsonElement>> entries() {
                return object.entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue()));
            }

            @Override
            public String toString() {
                return "MapLike[" + object + "]";
            }
        });
    }

    @Override
    public @NotNull JsonElement remove(final @NotNull JsonElement input, final @NotNull String key) {
        if (input instanceof final JsonObject object) {
            final var result = new JsonObject();
            object.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key))
                    .forEach(entry -> result.add(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }

    @Override
    public @NotNull DataResult<JsonElement> mergeToMap(final @NotNull JsonElement map, final @NotNull JsonElement key,
                                                       final @NotNull JsonElement value) {
        if (!map.isJsonObject() && map != empty()) {
            return DataResult.error("Cannot merge key " + key + " and value " + value + " in to non-map " + map + "!");
        }
        if (!key.isJsonPrimitive() || !key.getAsJsonPrimitive().isString()) return DataResult.error("Key " + key + " is not a string!");
        final var result = new JsonObject();
        if (map != empty()) map.getAsJsonObject().entrySet().forEach(entry -> result.add(entry.getKey(), entry.getValue()));
        result.add(key.getAsString(), value);
        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<JsonElement> mergeToMap(final @NotNull JsonElement map, final @NotNull MapLike<JsonElement> values) {
        if (!map.isJsonObject() && map != empty()) return DataResult.error("Cannot merge values " + values + " in to non-map " + map + "!");
        final var result = new JsonObject();
        if (map != empty()) map.getAsJsonObject().entrySet().forEach(entry -> result.add(entry.getKey(), entry.getValue()));
        final var missed = new ArrayList<JsonElement>();
        values.entries().forEach(entry -> {
            final var key = entry.first();
            if (!key.isJsonPrimitive() || !key.getAsJsonPrimitive().isString()) {
                missed.add(key);
                return;
            }
            result.add(key.getAsString(), entry.second());
        });
        if (!missed.isEmpty()) {
            return DataResult.error("Cannot merge values " + values + " in to map " + map + " as keys " + missed + " are not strings!");
        }
        return DataResult.success(result);
    }

    @Override
    public @NotNull JsonElement createNumber(final @NotNull Number number) {
        return new JsonPrimitive(number);
    }

    @Override
    public @NotNull JsonElement createBoolean(final boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull JsonElement createString(final @NotNull String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull JsonElement createList(final @NotNull Stream<JsonElement> input) {
        final var result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> listBuilder() {
        return new ArrayBuilder();
    }

    @Override
    public @NotNull JsonElement createMap(final @NotNull Stream<Pair<JsonElement, JsonElement>> map) {
        final var result = new JsonObject();
        map.forEach(entry -> result.add(entry.first().getAsString(), entry.second()));
        return result;
    }

    @Override
    public @NotNull RecordBuilder<JsonElement> mapBuilder() {
        return new JsonRecordBuilder();
    }

    @Override
    public <U> @NotNull U convertTo(final @NotNull DataOps<U> outOps, final @NotNull JsonElement input) {
        if (input.isJsonObject()) return convertMap(outOps, input);
        if (input.isJsonArray()) return convertList(outOps, input);
        if (input.isJsonNull()) return outOps.empty();
        final JsonPrimitive primitive = input.getAsJsonPrimitive();
        if (primitive.isString()) return outOps.createString(primitive.getAsString());
        if (primitive.isBoolean()) return outOps.createBoolean(primitive.getAsBoolean());
        final var value = primitive.getAsBigDecimal();
        try {
            final var longValue = value.longValueExact();
            if ((byte) longValue == longValue) return outOps.createByte((byte) longValue);
            if ((short) longValue == longValue) return outOps.createShort((short) longValue);
            if ((int) longValue == longValue) return outOps.createInt((int) longValue);
            return outOps.createLong(longValue);
        } catch (final ArithmeticException exception) {
            final var doubleValue = value.doubleValue();
            if ((float) doubleValue == doubleValue) return outOps.createFloat((float) doubleValue);
            return outOps.createDouble(doubleValue);
        }
    }

    @Override
    public String toString() {
        return "JSON";
    }

    private static @Nullable JsonElement orNull(final @Nullable JsonElement element) {
        return element == null || element.isJsonNull() ? null : element;
    }

    private static <R> @NotNull DataResult<R> error(final @NotNull String methodName, final @NotNull String name, final @NotNull JsonElement input) {
        return DataResult.error("Provided input " + input + " for " + methodName + " is not a " + name + "!");
    }
}
