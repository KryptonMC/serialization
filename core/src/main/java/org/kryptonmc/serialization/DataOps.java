/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
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

    @NotNull T empty();

    default boolean getBooleanValue(final @NotNull T input) {
        return getNumberValue(input).byteValue() != 0;
    }

    @NotNull Number getNumberValue(@NotNull T input);

    default @NotNull Number getNumberValue(final @NotNull T input, final @NotNull Number defaultValue) {
        try {
            return getNumberValue(input);
        } catch (final Exception ignored) {
            return defaultValue;
        }
    }

    @NotNull String getStringValue(@NotNull T input);

    default @NotNull T mergeToPrimitive(final @NotNull T prefix, final @NotNull T value) {
        if (!Objects.equals(prefix, empty())) throw new IllegalArgumentException("Cannot append primitive value " + value + " to " + prefix);
        return value;
    }

    @NotNull Stream<T> getStream(@NotNull T input);

    default @NotNull Consumer<Consumer<T>> getList(final @NotNull T input) {
        return getStream(input)::forEach;
    }

    @NotNull T mergeToList(@NotNull T list, @NotNull T value);

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

    @NotNull Stream<Pair<T, T>> getMapValues(@NotNull T input);

    default @NotNull Consumer<BiConsumer<T, T>> getMapEntries(final @NotNull T input) {
        final var values = getMapValues(input);
        return consumer -> values.forEach(value -> consumer.accept(value.first(), value.second()));
    }

    default @NotNull MapLike<T> getMap(final @NotNull T input) {
        return MapLike.forMap(getMapValues(input).collect(Pair.toMap()), this);
    }

    @NotNull T mergeToMap(@NotNull T map, @NotNull T key, @NotNull T value);

    default @NotNull T mergeToMap(final @NotNull T map, final @NotNull MapLike<T> values) {
        final AtomicReference<T> result = new AtomicReference<>(map);
        values.entries().forEach(entry -> result.setPlain(mergeToMap(result.getPlain(), entry.first(), entry.second())));
        return result.getPlain();
    }

    default @NotNull T mergeToMap(final @NotNull T map, final @NotNull Map<T, T> values) {
        return mergeToMap(map, MapLike.forMap(values, this));
    }

    @NotNull T createNumber(@NotNull Number number);

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

    @NotNull T createString(@NotNull String value);

    @NotNull T createList(@NotNull Stream<T> input);

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

    @NotNull T createMap(@NotNull Stream<Pair<T, T>> map);

    default @NotNull T createMap(final @NotNull Map<T, T> map) {
        return createMap(map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())));
    }

    default @NotNull RecordBuilder<T> mapBuilder() {
        return new RecordBuilder.Default<>(this);
    }

    <U> @NotNull U convertTo(@NotNull DataOps<U> outOps, @NotNull T input);

    default <U> @NotNull U convertList(final @NotNull DataOps<U> outOps, final @NotNull T input) {
        return outOps.createList(StreamUtil.orEmpty(() -> getStream(input)).map(element -> convertTo(outOps, element)));
    }

    default <U> @NotNull U convertMap(final @NotNull DataOps<U> outOps, final @NotNull T input) {
        return outOps.createMap(StreamUtil.orEmpty(() -> getMapValues(input))
                .map(entry -> Pair.of(convertTo(outOps, entry.first()), convertTo(outOps, entry.second()))));
    }
}
