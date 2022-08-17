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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/DynamicOps.java
 */
package org.kryptonmc.serialization;

import com.google.common.collect.ImmutableMap;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

public interface DataOps<T> {

    /**
     * Returns some empty type for these operations.
     *
     * <p>The empty type must be a constant singleton so that referential
     * equality to it will always work, and it must not be null, however it can
     * represent null, as is the case for JSON operations.</p>
     */
    @NotNull T empty();

    default @NotNull T emptyMap() {
        return createMap(ImmutableMap.of());
    }

    default @NotNull T emptyList() {
        return createList(Stream.empty());
    }

    default @NotNull DataResult<Boolean> getBooleanValue(final @NotNull T input) {
        return getNumberValue(input).map(number -> number.byteValue() != 0);
    }

    @NotNull DataResult<Number> getNumberValue(final @NotNull T input);

    default @NotNull Number getNumberValue(final @NotNull T input, final @NotNull Number defaultValue) {
        return getNumberValue(input).result().orElse(defaultValue);
    }

    @NotNull DataResult<String> getStringValue(final @NotNull T input);

    default @NotNull DataResult<T> mergeToPrimitive(final @NotNull T prefix, final @NotNull T value) {
        if (!Objects.equals(prefix, empty())) return DataResult.error("Cannot append primitive value " + value + " to " + prefix + "!");
        return DataResult.success(value);
    }

    @NotNull DataResult<Stream<T>> getStream(final @NotNull T input);

    default @NotNull DataResult<Consumer<Consumer<T>>> getList(final @NotNull T input) {
        return getStream(input).map(stream -> stream::forEach);
    }

    @NotNull DataResult<T> mergeToList(final @NotNull T list, final @NotNull T value);

    default @NotNull DataResult<T> mergeToList(final @NotNull T list, final @NotNull List<T> values) {
        var result = DataResult.success(list);
        for (final var value : values) {
            result = result.flatMap(r -> mergeToList(r, value));
        }
        return result;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default @NotNull DataResult<ByteBuffer> getByteBuffer(final @NotNull T input) {
        return getStream(input).flatMap(stream -> {
            if (stream.allMatch(element -> getNumberValue(element).result().isPresent())) {
                final var list = stream.toList();
                final var buffer = ByteBuffer.wrap(new byte[list.size()]);
                for (int i = 0; i < list.size(); i++) {
                    // We already checked earlier if this is present.
                    buffer.put(i, getNumberValue(list.get(i)).result().get().byteValue());
                }
                return DataResult.success(buffer);
            }
            return DataResult.error("Some elements in the given input " + input + " are not bytes!");
        });
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default @NotNull DataResult<IntStream> getIntStream(final @NotNull T input) {
        return getStream(input).flatMap(stream -> {
            if (stream.allMatch(element -> getNumberValue(element).result().isPresent())) {
                // We already checked earlier if this is present.
                return DataResult.success(stream.mapToInt(element -> getNumberValue(element).result().get().intValue()));
            }
            return DataResult.error("Some elements in the given input " + input + " are not integers!");
        });
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default @NotNull DataResult<LongStream> getLongStream(final @NotNull T input) {
        return getStream(input).flatMap(stream -> {
            if (stream.allMatch(element -> getNumberValue(element).result().isPresent())) {
                // We already checked earlier if this is present.
                return DataResult.success(stream.mapToLong(element -> getNumberValue(element).result().get().longValue()));
            }
            return DataResult.error("Some elements in the given input " + input + " are not longs!");
        });
    }

    @NotNull DataResult<Stream<Pair<T, T>>> getMapValues(final @NotNull T input);

    default @NotNull DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(final @NotNull T input) {
        return getMapValues(input).map(stream -> consumer -> stream.forEach(entry -> consumer.accept(entry.first(), entry.second())));
    }

    default @NotNull DataResult<MapLike<T>> getMap(final @NotNull T input) {
        return getMapValues(input).flatMap(stream -> {
            try {
                return DataResult.success(MapLike.forMap(stream.collect(Pair.toMap()), this));
            } catch (final IllegalStateException exception) {
                return DataResult.error("Error whilst trying to build map for input " + input + ": " + exception.getMessage());
            }
        });
    }

    default @NotNull T set(final @NotNull T input, final @NotNull String key, final @NotNull T value) {
        return mergeToMap(input, createString(key), value).result().orElse(input);
    }

    @NotNull T remove(final @NotNull T input, final @NotNull String key);

    @NotNull DataResult<T> mergeToMap(final @NotNull T map, final @NotNull T key, final @NotNull T value);

    default @NotNull DataResult<T> mergeToMap(final @NotNull T map, final @NotNull MapLike<T> values) {
        final AtomicReference<DataResult<T>> result = new AtomicReference<>(DataResult.success(map));
        values.entries().forEach(entry -> result.setPlain(result.getPlain().flatMap(r -> mergeToMap(r, entry.first(), entry.second()))));
        return result.getPlain();
    }

    default @NotNull DataResult<T> mergeToMap(final @NotNull T map, final @NotNull Map<T, T> values) {
        return mergeToMap(map, MapLike.forMap(values, this));
    }

    @NotNull T createNumber(final @NotNull Number number);

    default @NotNull T createBoolean(final boolean value) {
        return createByte((byte) (value ? 1 : 0));
    }

    default @NotNull T createByte(final byte value) {
        return createNumber(value);
    }

    default @NotNull T createShort(final short value) {
        return createNumber(value);
    }

    default T createInt(final int value) {
        return createNumber(value);
    }

    default T createLong(final long value) {
        return createNumber(value);
    }

    default T createFloat(final float value) {
        return createNumber(value);
    }

    default T createDouble(final double value) {
        return createNumber(value);
    }

    @NotNull T createString(final @NotNull String value);

    @NotNull T createList(final @NotNull Stream<T> input);

    default @NotNull T createByteList(final @NotNull ByteBuffer input) {
        return createList(IntStream.range(0, input.capacity()).mapToObj(index -> createByte(input.get(index))));
    }

    default @NotNull T createIntList(final @NotNull IntStream input) {
        return createList(input.mapToObj(this::createInt));
    }

    default @NotNull T createLongList(final @NotNull LongStream input) {
        return createList(input.mapToObj(this::createLong));
    }

    default @NotNull ListBuilder<T> listBuilder() {
        return new ListBuilder.Default<>(this);
    }

    @NotNull T createMap(final @NotNull Stream<Pair<T, T>> map);

    default @NotNull T createMap(final @NotNull Map<T, T> map) {
        return createMap(map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())));
    }

    default @NotNull RecordBuilder<T> mapBuilder() {
        return new RecordBuilder.Default<>(this);
    }

    <U> @NotNull U convertTo(final @NotNull DataOps<U> outOps, final @NotNull T input);

    default <U> @NotNull U convertList(final @NotNull DataOps<U> outOps, final @NotNull T input) {
        return outOps.createList(getStream(input).result().orElse(Stream.empty()).map(element -> convertTo(outOps, element)));
    }

    default <U> @NotNull U convertMap(final @NotNull DataOps<U> outOps, final @NotNull T input) {
        return outOps.createMap(getMapValues(input).result().orElse(Stream.empty())
                .map(entry -> Pair.of(convertTo(outOps, entry.first()), convertTo(outOps, entry.second()))));
    }
}
