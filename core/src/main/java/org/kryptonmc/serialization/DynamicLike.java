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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/DynamicLike.java
 */
package org.kryptonmc.serialization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

/**
 * An abstraction over {@link Dynamic} and {@link OptionalDynamic}.
 *
 * @param <T> The data type.
 */
public abstract sealed class DynamicLike<T> permits Dynamic, OptionalDynamic {

    protected final DataOps<T> ops;

    protected DynamicLike(final DataOps<T> ops) {
        this.ops = ops;
    }

    /**
     * Gets the operations for the data.
     *
     * @return The data operations.
     */
    public @NotNull DataOps<T> ops() {
        return ops;
    }

    /**
     * Gets this dynamic as a number, if it is a number.
     *
     * @return The number value.
     */
    public abstract @NotNull DataResult<Number> asNumber();

    /**
     * Gets this dynamic as a string, if it is a string.
     *
     * @return The string value.
     */
    public abstract @NotNull DataResult<String> asString();

    /**
     * Gets this dynamic as a stream, if it can be converted to a stream.
     *
     * @return The stream value.
     */
    public abstract @NotNull DataResult<Stream<Dynamic<T>>> asOptionalStream();

    /**
     * Gets this dynamic as a map, if it can be converted to a map.
     *
     * @return The map value.
     */
    public abstract @NotNull DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asOptionalMap();

    /**
     * Gets this dynamic as a byte buffer, if it can be converted to a byte
     * buffer.
     *
     * @return The byte buffer value.
     */
    public abstract @NotNull DataResult<ByteBuffer> asOptionalByteBuffer();

    /**
     * Gets this dynamic as an int stream, if it can be converted to an int
     * stream.
     *
     * @return The int stream value.
     */
    public abstract @NotNull DataResult<IntStream> asOptionalIntStream();

    /**
     * Gets this dynamic as a long stream, if it can be converted to a long
     * stream.
     *
     * @return The long stream value.
     */
    public abstract @NotNull DataResult<LongStream> asOptionalLongStream();

    /**
     * Gets the value associated with the given key.
     *
     * @param key The key.
     * @return The associated value.
     */
    public abstract @NotNull OptionalDynamic<T> get(final @NotNull String key);

    /**
     * Decodes this dynamic value using the given decoder.
     *
     * @param decoder The decoder to decode this dynamic with.
     * @param <A> The decoded type.
     * @return The decoded result.
     */
    public abstract <A> @NotNull DataResult<Pair<A, T>> decode(final @NotNull Decoder<? extends A> decoder);

    /**
     * Converts this dynamic value to a list, if it can be converted to a list,
     * using the given deserializer to deserialize the elements.
     *
     * @param deserializer The deserializer to deserialize the elements with.
     * @param <U> The list element type.
     * @return The result of converting this dynamic to a list.
     */
    public final <U> @NotNull DataResult<List<U>> asOptionalList(final @NotNull Function<Dynamic<T>, U> deserializer) {
        return asOptionalStream().map(stream -> stream.map(deserializer).collect(Collectors.toList()));
    }

    /**
     * Converts this dynamic value to a map, if it can be converted to a map,
     * using the given key deserializer to deserialize the keys, and the given
     * value deserializer to deserialize the values.
     *
     * @param keyDeserializer The deserializer to deserialize the keys with.
     * @param valueDeserializer The deserializer to deserialize the values
     *                          with.
     * @param <K> The map key type.
     * @param <V> The map value type.
     * @return The result of converting this dynamic to a list.
     */
    public final <K, V> @NotNull DataResult<Map<K, V>> asOptionalMap(final @NotNull Function<Dynamic<T>, K> keyDeserializer,
                                                                     final @NotNull Function<Dynamic<T>, V> valueDeserializer) {
        return asOptionalMap().map(map -> {
            final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
            map.forEach(entry -> builder.put(keyDeserializer.apply(entry.first()), valueDeserializer.apply(entry.second())));
            return builder.build();
        });
    }

    /**
     * Decodes this dynamic value using the given decoder.
     *
     * @param decoder The decoder to decode this dynamic with.
     * @param <A> The decoded type.
     * @return The decoded result.
     */
    public final <A> @NotNull DataResult<A> read(final @NotNull Decoder<? extends A> decoder) {
        return decode(decoder).map(Pair::first);
    }

    /**
     * Gets this dynamic as a number, if it is a number, or returns the given
     * default value if this dynamic is not a number.
     *
     * @param defaultValue The default value.
     * @return The number value, or the default value if this dynamic is not a
     *         number.
     */
    public final @NotNull Number asNumber(final @NotNull Number defaultValue) {
        return asNumber().result().orElse(defaultValue);
    }

    /**
     * Gets this dynamic as a boolean, if it is a boolean, or returns the given
     * default value if this dynamic is not a boolean.
     *
     * @param defaultValue The default value.
     * @return The boolean value, or the default value if this dynamic is not a
     *         boolean.
     */
    public final boolean asBoolean(final boolean defaultValue) {
        return asNumber(defaultValue ? 1 : 0).intValue() != 0;
    }

    /**
     * Gets this dynamic as a byte, if it is a byte, or returns the given
     * default value if this dynamic is not a byte.
     *
     * @param defaultValue The default value.
     * @return The byte value, or the default value if this dynamic is not a
     *         byte.
     */
    public final byte asByte(final byte defaultValue) {
        return asNumber(defaultValue).byteValue();
    }

    /**
     * Gets this dynamic as a short, if it is a short, or returns the given
     * default value if this dynamic is not a short.
     *
     * @param defaultValue The default value.
     * @return The short value, or the default value if this dynamic is not a
     *         short.
     */
    public final short asShort(final short defaultValue) {
        return asNumber(defaultValue).shortValue();
    }

    /**
     * Gets this dynamic as an integer, if it is an integer, or returns the
     * given default value if this dynamic is not an integer.
     *
     * @param defaultValue The default value.
     * @return The integer value, or the default value if this dynamic is not
     *         an integer.
     */
    public final int asInt(final int defaultValue) {
        return asNumber(defaultValue).intValue();
    }

    /**
     * Gets this dynamic as a long, if it is a long, or returns the given
     * default value if this dynamic is not a long.
     *
     * @param defaultValue The default value.
     * @return The long value, or the default value if this dynamic is not a
     *         long.
     */
    public final long asLong(final long defaultValue) {
        return asNumber(defaultValue).longValue();
    }

    /**
     * Gets this dynamic as a float, if it is a float, or returns the given
     * default value if this dynamic is not a float.
     *
     * @param defaultValue The default value.
     * @return The float value, or the default value if this dynamic is not a
     *         float.
     */
    public final float asFloat(final float defaultValue) {
        return asNumber(defaultValue).floatValue();
    }

    /**
     * Gets this dynamic as a double, if it is a double, or returns the given
     * default value if this dynamic is not a double.
     *
     * @param defaultValue The default value.
     * @return The double value, or the default value if this dynamic is not a
     *         double.
     */
    public final double asDouble(final double defaultValue) {
        return asNumber(defaultValue).doubleValue();
    }

    /**
     * Gets this dynamic as a string, if it is a string, or returns the given
     * default value if this dynamic is not a string.
     *
     * @param defaultValue The default value.
     * @return The string value, or the default value if this dynamic is not a
     *         string.
     */
    public final @NotNull String asString(final @NotNull String defaultValue) {
        return asString().result().orElse(defaultValue);
    }

    /**
     * Gets this dynamic as a stream, if it can be converted to a stream, or
     * returns an empty stream.
     *
     * @return The stream value.
     */
    public final @NotNull Stream<Dynamic<T>> asStream() {
        return asOptionalStream().result().orElseGet(Stream::empty);
    }

    /**
     * Gets this dynamic as a byte buffer, if it can be converted to a byte
     * buffer, or returns an empty buffer.
     *
     * @return The byte buffer value.
     */
    public final @NotNull ByteBuffer asByteBuffer() {
        return asOptionalByteBuffer().result().orElseGet(() -> ByteBuffer.wrap(new byte[0]));
    }

    /**
     * Gets this dynamic as an int stream, if it can be converted to an int
     * stream, or returns an empty stream.
     *
     * @return The int stream value.
     */
    public final @NotNull IntStream asIntStream() {
        return asOptionalIntStream().result().orElseGet(IntStream::empty);
    }

    /**
     * Gets this dynamic as a long stream, if it can be converted to a long
     * stream, or returns an empty stream.
     *
     * @return The long stream value.
     */
    public final @NotNull LongStream asLongStream() {
        return asOptionalLongStream().result().orElseGet(LongStream::empty);
    }

    /**
     * Gets this dynamic as a list, if it can be converted to a list, using the
     * given deserializer to deserialize the elements, or returns an empty
     * list.
     *
     * @param deserializer The deserializer to deserialize the elements with.
     * @param <U> The list element type.
     * @return The list value.
     */
    public final <U> @NotNull List<U> asList(final @NotNull Function<Dynamic<T>, U> deserializer) {
        return asOptionalList(deserializer).result().orElseGet(ImmutableList::of);
    }

    /**
     * Gets this dynamic as a map, if it can be converted to a map, using the
     * given key deserializer to deserialize the keys, and the given value
     * deserializer to deserialize the values, or returns an empty map.
     *
     * @param keyDeserializer The deserializer to deserialize the keys with.
     * @param valueDeserializer The deserializer to deserialize the values
     *                          with.
     * @param <K> The map key type.
     * @param <V> The map value type.
     * @return The map value.
     */
    public final <K, V> @NotNull Map<K, V> asMap(final @NotNull Function<Dynamic<T>, K> keyDeserializer,
                                                 final @NotNull Function<Dynamic<T>, V> valueDeserializer) {
        return asOptionalMap(keyDeserializer, valueDeserializer).result().orElseGet(ImmutableMap::of);
    }
}
