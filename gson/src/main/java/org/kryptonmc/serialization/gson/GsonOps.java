/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.ListBuilder;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

public final class GsonOps implements DataOps<JsonElement> {

    public static final @NotNull GsonOps INSTANCE = new GsonOps();

    private GsonOps() {
    }

    @Override
    public @NotNull JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public boolean getBooleanValue(final @NotNull JsonElement input) {
        if (input.isJsonPrimitive()) {
            if (input.getAsJsonPrimitive().isBoolean()) return input.getAsBoolean();
            if (input.getAsJsonPrimitive().isNumber()) return input.getAsNumber().byteValue() != 0;
        }
        throw new IllegalArgumentException("Provided input for getBooleanValue is not a boolean! Input: " + input);
    }

    @Override
    public @NotNull Number getNumberValue(final @NotNull JsonElement input) {
        if (input.isJsonPrimitive()) {
            if (input.getAsJsonPrimitive().isNumber()) return input.getAsNumber();
            if (input.getAsJsonPrimitive().isBoolean()) return input.getAsBoolean() ? 1 : 0;
        }
        if (input.isJsonPrimitive() && input.getAsJsonPrimitive().isBoolean()) return input.getAsJsonPrimitive().getAsBoolean() ? 1 : 0;
        throw new IllegalArgumentException("Provided input for getNumberValue is not a number! Input: " + input);
    }

    @Override
    public @NotNull String getStringValue(final @NotNull JsonElement input) {
        if (input.isJsonPrimitive() && input.getAsJsonPrimitive().isString()) return input.getAsString();
        throw new IllegalArgumentException("Provided input for getStringValue is not a string! Input: " + input);
    }

    @Override
    public @NotNull Stream<JsonElement> getStream(final @NotNull JsonElement input) {
        if (input.isJsonArray()) {
            return StreamSupport.stream(input.getAsJsonArray().spliterator(), false).map(JsonUtil::orNull);
        }
        throw new IllegalArgumentException("Provided input for getStream is not a json array! Input: " + input);
    }

    @Override
    public @NotNull Consumer<Consumer<JsonElement>> getList(final @NotNull JsonElement input) {
        if (input.isJsonArray()) {
            return consumer -> {
                for (final var element : input.getAsJsonArray()) {
                    consumer.accept(JsonUtil.orNull(element));
                }
            };
        }
        throw new IllegalArgumentException("Provided input for getList is not a json array! Input: " + input);
    }

    @Override
    public @NotNull JsonElement mergeToList(final @NotNull JsonElement list, final @NotNull JsonElement value) {
        if (!list.isJsonArray() && list != empty()) error("Cannot merge value " + value + " in to non-list " + list + "!");
        final JsonArray result = new JsonArray();
        if (list != empty()) result.addAll(list.getAsJsonArray());
        result.add(value);
        return result;
    }

    @Override
    public @NotNull JsonElement mergeToList(final @NotNull JsonElement list, final @NotNull List<JsonElement> values) {
        if (!list.isJsonArray() && list != empty()) error("Cannot merge values " + values + " in to non-list " + list + "!");
        final JsonArray result = new JsonArray();
        if (list != empty()) result.addAll(list.getAsJsonArray());
        values.forEach(result::add);
        return result;
    }

    @Override
    public @NotNull Stream<Pair<JsonElement, JsonElement>> getMapValues(final @NotNull JsonElement input) {
        if (!input.isJsonObject()) error("Provided input for getMapValues is not a json object! Input: " + input);
        return input.getAsJsonObject().entrySet().stream()
                .map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), JsonUtil.orNull(entry.getValue())));
    }

    @Override
    public @NotNull Consumer<BiConsumer<JsonElement, JsonElement>> getMapEntries(final @NotNull JsonElement input) {
        if (!input.isJsonObject()) error("Provided input for getMapEntries is not a json object! Input: " + input);
        return consumer -> {
            for (final var entry : input.getAsJsonObject().entrySet()) {
                consumer.accept(createString(entry.getKey()), JsonUtil.orNull(entry.getValue()));
            }
        };
    }

    @Override
    public @NotNull MapLike<JsonElement> getMap(final @NotNull JsonElement input) {
        if (!input.isJsonObject()) error("Provided input for getMap is not a json object! Input: " + input);
        final JsonObject object = input.getAsJsonObject();
        return new MapLike<>() {
            @Override
            public @Nullable JsonElement get(final @NotNull JsonElement key) {
                return get(key.getAsString());
            }

            @Override
            public @Nullable JsonElement get(final @NotNull String key) {
                return JsonUtil.orNull(object.get(key));
            }

            @Override
            public @NotNull Stream<Pair<JsonElement, JsonElement>> entries() {
                return object.entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue()));
            }

            @Override
            public String toString() {
                return "MapLike[" + object + "]";
            }
        };
    }

    @Override
    public @NotNull JsonElement mergeToMap(final @NotNull JsonElement map, final @NotNull JsonElement key, final @NotNull JsonElement value) {
        if (!map.isJsonObject() && map != empty()) error("Cannot merge key " + key + " and value " + value + " in to non-map " + map + "!");
        if (!key.isJsonPrimitive() || !key.getAsJsonPrimitive().isString()) error("Key " + key + " is not a string!");
        final JsonObject result = new JsonObject();
        if (map != empty()) map.getAsJsonObject().entrySet().forEach(entry -> result.add(entry.getKey(), entry.getValue()));
        result.add(key.getAsString(), value);
        return result;
    }

    @Override
    public @NotNull JsonElement mergeToMap(final @NotNull JsonElement map, final @NotNull MapLike<JsonElement> values) {
        if (!map.isJsonObject() && map != empty()) error("Cannot merge values " + values + " in to non-map " + map + "!");
        final JsonObject result = new JsonObject();
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
        if (!missed.isEmpty()) error("Cannot merge values " + values + " in to map " + map + " as keys " + missed + " are not strings!");
        return result;
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
        final JsonArray result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public @NotNull ListBuilder<JsonElement> listBuilder() {
        return new ArrayBuilder();
    }

    @Override
    public @NotNull JsonElement createMap(final @NotNull Stream<Pair<JsonElement, JsonElement>> map) {
        final JsonObject result = new JsonObject();
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
        final BigDecimal value = primitive.getAsBigDecimal();
        try {
            final long longValue = value.longValueExact();
            if ((byte) longValue == longValue) return outOps.createByte((byte) longValue);
            if ((short) longValue == longValue) return outOps.createShort((short) longValue);
            if ((int) longValue == longValue) return outOps.createInt((int) longValue);
            return outOps.createLong(longValue);
        } catch (final ArithmeticException exception) {
            final double doubleValue = value.doubleValue();
            if ((float) doubleValue == doubleValue) return outOps.createFloat((float) doubleValue);
            return outOps.createDouble(doubleValue);
        }
    }

    @Override
    public String toString() {
        return "JSON";
    }

    private static void error(final @NotNull String message) {
        throw new IllegalArgumentException(message);
    }
}
