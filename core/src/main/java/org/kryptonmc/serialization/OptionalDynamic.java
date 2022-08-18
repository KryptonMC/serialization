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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/OptionalDynamic.java
 */
package org.kryptonmc.serialization;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

/**
 * A dynamic that wraps {@link Dynamic} in a {@link DataResult}.
 *
 * @param <T> The data type.
 */
public final class OptionalDynamic<T> extends DynamicLike<T> {

    private final DataResult<Dynamic<T>> delegate;

    /**
     * Creates a new optional dynamic with the given data operations and
     * dynamic delegate result.
     *
     * @param ops The data operations.
     * @param delegate The dynamic delegate result.
     */
    public OptionalDynamic(final @NotNull DataOps<T> ops, final @NotNull DataResult<Dynamic<T>> delegate) {
        super(ops);
        this.delegate = delegate;
    }

    /**
     * Gets the delegate dynamic wrapped in a data result.
     *
     * @return The delegate dynamic.
     */
    public @NotNull DataResult<Dynamic<T>> get() {
        return delegate;
    }

    /**
     * Gets the backing dynamic value, if present.
     *
     * @return The backing dynamic.
     */
    public @NotNull Optional<Dynamic<T>> result() {
        return delegate.result();
    }

    /**
     * Maps this optional dynamic by mapping the delegate value with the given
     * mapper.
     *
     * @param mapper The mapper to map the value with.
     * @param <U> The new data type.
     * @return The resulting mapped dynamic.
     */
    public <U> @NotNull DataResult<U> map(final @NotNull Function<? super Dynamic<T>, ? extends U> mapper) {
        return delegate.map(mapper);
    }

    /**
     * Maps this optional dynamic by mapping the delegate value with the given
     * mapper.
     *
     * <p>This differs from {@link #map(Function)} in that the function here
     * returns a {@link DataResult}, which allows errors and other information
     * to be carried from other results in to a new result mapped from the
     * delegate.</p>
     *
     * @param mapper The mapper to map the value with.
     * @param <U> The new data type.
     * @return The resulting mapped dynamic.
     */
    public <U> @NotNull DataResult<U> flatMap(final @NotNull Function<? super Dynamic<T>, ? extends DataResult<U>> mapper) {
        return delegate.flatMap(mapper);
    }

    @Override
    public @NotNull DataResult<Number> asNumber() {
        return flatMap(DynamicLike::asNumber);
    }

    @Override
    public @NotNull DataResult<String> asString() {
        return flatMap(DynamicLike::asString);
    }

    @Override
    public @NotNull DataResult<Stream<Dynamic<T>>> asOptionalStream() {
        return flatMap(DynamicLike::asOptionalStream);
    }

    @Override
    public @NotNull DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asOptionalMap() {
        return flatMap(DynamicLike::asOptionalMap);
    }

    @Override
    public @NotNull DataResult<ByteBuffer> asOptionalByteBuffer() {
        return flatMap(DynamicLike::asOptionalByteBuffer);
    }

    @Override
    public @NotNull DataResult<IntStream> asOptionalIntStream() {
        return flatMap(DynamicLike::asOptionalIntStream);
    }

    @Override
    public @NotNull DataResult<LongStream> asOptionalLongStream() {
        return flatMap(DynamicLike::asOptionalLongStream);
    }

    @Override
    public @NotNull OptionalDynamic<T> get(final @NotNull String key) {
        return new OptionalDynamic<>(ops, delegate.flatMap(map -> map.get(key).delegate));
    }

    /**
     * Gets the delegate dynamic contained within this optional dynamic, if it
     * is present, else returns a dynamic with the empty list value.
     *
     * @return The resulting list dynamic.
     */
    public @NotNull Dynamic<T> orElseEmptyList() {
        return result().orElseGet(() -> new Dynamic<>(ops, ops.emptyList()));
    }

    /**
     * Gets the delegate dynamic contained within this optional dynamic, if it
     * is present, else returns a dynamic with the empty map value.
     *
     * @return The resulting map dynamic.
     */
    public @NotNull Dynamic<T> orElseEmptyMap() {
        return result().orElseGet(() -> new Dynamic<>(ops, ops.emptyMap()));
    }

    @Override
    public <A> @NotNull DataResult<Pair<A, T>> decode(final @NotNull Decoder<? extends A> decoder) {
        return delegate.flatMap(t -> t.decode(decoder));
    }
}
