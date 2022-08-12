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

    default boolean getBooleanValue(final @NotNull T input) {
        return getNumberValue(input).byteValue() != 0;
    }

    @NotNull Number getNumberValue(final @NotNull T input);

    default @NotNull Number getNumberValue(final @NotNull T input, final @NotNull Number defaultValue) {
        try {
            return getNumberValue(input);
        } catch (final Exception ignored) {
            return defaultValue;
        }
    }

    @NotNull String getStringValue(final @NotNull T input);

    default @NotNull T mergeToPrimitive(final @NotNull T prefix, final @NotNull T value) {
        if (!Objects.equals(prefix, empty())) throw new IllegalArgumentException("Cannot append primitive value " + value + " to " + prefix);
        return value;
    }

    @NotNull Stream<T> getStream(final @NotNull T input);

    default @NotNull Consumer<Consumer<T>> getList(final @NotNull T input) {
        return getStream(input)::forEach;
    }

    @NotNull T mergeToList(final @NotNull T list, final @NotNull T value);

    default @NotNull T mergeToList(final @NotNull T list, final @NotNull List<T> values) {
        var result = list;
        for (final var value : values) {
            result = mergeToList(result, value);
        }
        return result;
    }

    default @NotNull ByteBuffer getByteBuffer(final @NotNull T input) {
        final var stream = getStream(input);
        if (StreamUtil.noneThrow(stream, this::getNumberValue)) {
            final var list = stream.toList();
            final var buffer = ByteBuffer.wrap(new byte[list.size()]);
            for (int i = 0; i < list.size(); i++) {
                buffer.put(i, getNumberValue(list.get(i)).byteValue());
            }
            return buffer;
        }
        throw new IllegalArgumentException("Some elements in the given input are not bytes! Input: " + input);
    }

    default @NotNull IntStream getIntStream(final @NotNull T input) {
        final var stream = getStream(input);
        if (StreamUtil.noneThrow(stream, this::getNumberValue)) return stream.mapToInt(element -> getNumberValue(element).intValue());
        throw new IllegalArgumentException("Some elements in the given input are not integers! Input: " + input);
    }

    default @NotNull LongStream getLongStream(final @NotNull T input) {
        final var stream = getStream(input);
        if (StreamUtil.noneThrow(stream, this::getNumberValue)) return stream.mapToLong(element -> getNumberValue(element).longValue());
        throw new IllegalArgumentException("Some elements in the given input are not longs! Input: " + input);
    }

    @NotNull Stream<Pair<T, T>> getMapValues(final @NotNull T input);

    default @NotNull Consumer<BiConsumer<T, T>> getMapEntries(final @NotNull T input) {
        final var values = getMapValues(input);
        return consumer -> values.forEach(value -> consumer.accept(value.first(), value.second()));
    }

    default @NotNull MapLike<T> getMap(final @NotNull T input) {
        return MapLike.forMap(getMapValues(input).collect(Pair.toMap()), this);
    }

    @NotNull T mergeToMap(final @NotNull T map, final @NotNull T key, final @NotNull T value);

    default @NotNull T mergeToMap(final @NotNull T map, final @NotNull MapLike<T> values) {
        final AtomicReference<T> result = new AtomicReference<>(map);
        values.entries().forEach(entry -> result.setPlain(mergeToMap(result.getPlain(), entry.first(), entry.second())));
        return result.getPlain();
    }

    default @NotNull T mergeToMap(final @NotNull T map, final @NotNull Map<T, T> values) {
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
        return outOps.createList(StreamUtil.orEmpty(() -> getStream(input)).map(element -> convertTo(outOps, element)));
    }

    default <U> @NotNull U convertMap(final @NotNull DataOps<U> outOps, final @NotNull T input) {
        return outOps.createMap(StreamUtil.orEmpty(() -> getMapValues(input))
                .map(entry -> Pair.of(convertTo(outOps, entry.first()), convertTo(outOps, entry.second()))));
    }
}
