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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/Dynamic.java
 */
package org.kryptonmc.serialization;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.Pair;

/**
 * An abstract type that represents some generic data type.
 *
 * @param <T> The data type.
 */
public final class Dynamic<T> extends DynamicLike<T> {

    private final T value;

    /**
     * Creates a new dynamic with the given operations and the empty value from
     * the operations.
     *
     * @param ops The data operations.
     */
    public Dynamic(final @NotNull DataOps<T> ops) {
        this(ops, ops.empty());
    }

    /**
     * Creates a new dynamic with the given operations and value.
     *
     * <p>If the value is null, the empty value from the operations will be
     * used.</p>
     *
     * @param ops The data operations.
     * @param value The value.
     */
    public Dynamic(final @NotNull DataOps<T> ops, final @Nullable T value) {
        super(ops);
        this.value = value == null ? ops.empty() : value;
    }

    /**
     * Gets the backing value of this dynamic.
     *
     * @return The backing value.
     */
    public @NotNull T value() {
        return value;
    }

    /**
     * Maps this dynamic to a new dynamic by applying the given mapper to the
     * backing value contained within this dynamic.
     *
     * @param mapper The mapper to apply to the backing value.
     * @return The resulting dynamic.
     */
    public @NotNull Dynamic<T> map(final @NotNull Function<? super T, ? extends T> mapper) {
        return new Dynamic<>(ops, mapper.apply(value));
    }

    @Override
    public @NotNull DataResult<Number> asNumber() {
        return ops.getNumberValue(value);
    }

    @Override
    public @NotNull DataResult<String> asString() {
        return ops.getStringValue(value);
    }

    @Override
    public @NotNull DataResult<Stream<Dynamic<T>>> asOptionalStream() {
        return ops.getStream(value).map(stream -> stream.map(element -> new Dynamic<>(ops, element)));
    }

    @Override
    public @NotNull DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asOptionalMap() {
        return ops.getMapValues(value)
                .map(values -> values.map(entry -> Pair.of(new Dynamic<>(ops, entry.first()), new Dynamic<>(ops, entry.second()))));
    }

    @Override
    public @NotNull DataResult<ByteBuffer> asOptionalByteBuffer() {
        return ops.getByteBuffer(value);
    }

    @Override
    public @NotNull DataResult<IntStream> asOptionalIntStream() {
        return ops.getIntStream(value);
    }

    @Override
    public @NotNull DataResult<LongStream> asOptionalLongStream() {
        return ops.getLongStream(value);
    }

    @Override
    public @NotNull OptionalDynamic<T> get(final @NotNull String key) {
        return new OptionalDynamic<>(ops, ops.getMap(value).flatMap(map -> {
            final var value = map.get(key);
            if (value == null) return DataResult.error("Cannot find key " + key + " in map " + map + "!");
            return DataResult.success(new Dynamic<>(ops, value));
        }));
    }

    /**
     * Creates a new dynamic with the result of setting the value for the given
     * key in the backing value for this dynamic.
     *
     * @param key The key.
     * @param value The value.
     * @return The resulting dynamic.
     */
    public @NotNull Dynamic<T> set(final @NotNull String key, final @NotNull Dynamic<?> value) {
        return map(v -> ops.set(v, key, value.cast(ops)));
    }

    /**
     * Creates a new dynamic with the result of removing the value for the
     * given key from the backing value for this dynamic.
     *
     * @param key The key.
     * @return The resulting dynamic.
     */
    public @NotNull Dynamic<T> remove(final @NotNull String key) {
        return map(v -> ops.remove(v, key));
    }

    /**
     * Converts this dynamic to a new dynamic with the result of converting the
     * backing value using the given out operations.
     *
     * @param outOps The out data operations.
     * @param <R> The new data type.
     * @return The resulting dynamic.
     */
    public <R> @NotNull Dynamic<R> convert(final @NotNull DataOps<R> outOps) {
        return new Dynamic<>(outOps, convert(ops, outOps, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> @NotNull DataResult<Pair<A, T>> decode(final @NotNull Decoder<? extends A> decoder) {
        return (DataResult<Pair<A, T>>) (Object) decoder.decode(value, ops);
    }

    @SuppressWarnings("unchecked")
    private <U> @NotNull Dynamic<U> castTyped(final @NotNull DataOps<U> ops) {
        if (!Objects.equals(this.ops, ops)) throw new IllegalStateException("Dynamic type doesn't match");
        return (Dynamic<U>) this;
    }

    private <U> @NotNull U cast(final @NotNull DataOps<U> ops) {
        return castTyped(ops).value();
    }

    @SuppressWarnings("unchecked")
    private static <S, T> @NotNull T convert(final @NotNull DataOps<S> inOps, final @NotNull DataOps<T> outOps, final @NotNull S input) {
        if (Objects.equals(inOps, outOps)) return (T) input;
        return inOps.convertTo(outOps, input);
    }
}
