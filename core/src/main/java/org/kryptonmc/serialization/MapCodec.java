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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/MapCodec.java
 */
package org.kryptonmc.serialization;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.codecs.EitherMapCodec;
import org.kryptonmc.serialization.codecs.PairMapCodec;
import org.kryptonmc.serialization.codecs.RecordCodecBuilder;
import org.kryptonmc.serialization.codecs.SimpleMapCodec;
import org.kryptonmc.util.Either;
import org.kryptonmc.util.Pair;

/**
 * A specialization of {@link Codec} that uses {@link MapLike} values as input
 * and {@link RecordBuilder}s as output.
 *
 * @param <A> The value type.
 */
public interface MapCodec<A> extends MapEncoder<A>, MapDecoder<A> {

    /**
     * Creates a new map codec that delegates the encoding to the given encoder
     * and the decoding to the given decoder.
     *
     * @param encoder The encoder to encode with.
     * @param decoder The decoder to decode with.
     * @param <A> The value type.
     * @return A new map codec.
     */
    static <A> @NotNull MapCodec<A> of(final @NotNull MapEncoder<A> encoder, final @NotNull MapDecoder<A> decoder) {
        return of(encoder, decoder, () -> "MapCodec[" + encoder + " " + decoder + "]");
    }

    /**
     * Creates a new map codec that delegates the encoding to the given encoder
     * and the decoding to the given decoder, with the result of applying the
     * given name supplier as the given name, for debugging purposes.
     *
     * @param encoder The encoder to encode with.
     * @param decoder The decoder to decode with.
     * @param name The name supplier to apply to get the name.
     * @param <A> The value type.
     * @return A new map codec.
     */
    static <A> @NotNull MapCodec<A> of(final @NotNull MapEncoder<A> encoder, final @NotNull MapDecoder<A> decoder,
                                       final @NotNull Supplier<String> name) {
        Objects.requireNonNull(encoder, "encoder");
        Objects.requireNonNull(decoder, "decoder");
        Objects.requireNonNull(name, "name");
        return new MapCodec<>() {
            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return decoder.decode(input, ops);
            }

            @Override
            public <T> @NotNull RecordBuilder<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
                return encoder.encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return name.get();
            }
        };
    }

    /**
     * Creates a new map codec that returns the default value when decoding,
     * and does nothing when encoding.
     *
     * <p>This is equivalent to of(Encoder.empty(), Decoder.unit(defaultValue))</p>
     *
     * @param defaultValue The value to return when decoding.
     * @param <A> The value type.
     * @return A new unit map codec.
     */
    static <A> @NotNull MapCodec<A> unit(final @NotNull A defaultValue) {
        return of(Encoder.empty(), Decoder.unit(defaultValue));
    }

    /**
     * Creates a new map codec that returns the result of applying the default
     * value supplier when decoding, and does nothing when encoding.
     *
     * <p>This is equivalent to of(Encoder.empty(), Decoder.unit(defaultValue))</p>
     *
     * @param defaultValue The supplier to get the result of when decoding.
     * @param <A> The value type.
     * @return A new unit map codec.
     */
    static <A> @NotNull MapCodec<A> unit(final @NotNull Supplier<A> defaultValue) {
        return of(Encoder.empty(), Decoder.unit(defaultValue));
    }

    /**
     * Creates a new map codec that encodes/decodes a pair of values using the
     * given first and second map codec.
     *
     * @param first The first codec.
     * @param second The second codec.
     * @param <F> The first type.
     * @param <S> The second type.
     * @return A new pair map codec.
     * @see PairMapCodec
     */
    static <F, S> @NotNull MapCodec<Pair<F, S>> pair(final @NotNull MapCodec<F> first, final @NotNull MapCodec<S> second) {
        return new PairMapCodec<>(first, second);
    }

    /**
     * Creates a new map codec that attempts to encode/decode the left value,
     * else falls back to the right value.
     *
     * @param left The left codec.
     * @param right The right codec.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A new either map codec.
     * @see EitherMapCodec
     */
    static <L, R> @NotNull MapCodec<Either<L, R>> either(final @NotNull MapCodec<L> left, final @NotNull MapCodec<R> right) {
        return new EitherMapCodec<>(left, right);
    }

    /**
     * Creates a new map codec that encodes/decodes a map of values using the
     * key codec to process the keys and the value codec to process the values.
     *
     * @param keyCodec The key codec.
     * @param valueCodec The value codec.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return A new map codec.
     */
    static <K, V> @NotNull MapCodec<Map<K, V>> map(final @NotNull Codec<K> keyCodec, final @NotNull Codec<V> valueCodec) {
        return new SimpleMapCodec<>(keyCodec, valueCodec);
    }

    /**
     * Converts this map codec in to a {@link RecordCodecBuilder} that returns
     * the result of getting the value from the complex type using the given
     * getter function.
     *
     * @param getter The getter function to get the value with.
     * @param <O> The complex type.
     * @return A new getting codec.
     */
    @ApiStatus.NonExtendable
    default <O> @NotNull RecordCodecBuilder<O, A> getting(final @NotNull Function<O, A> getter) {
        return RecordCodecBuilder.of(getter, this);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> stable() {
        return withLifecycle(Lifecycle.stable());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> deprecated(final int since) {
        return withLifecycle(Lifecycle.deprecated(since));
    }

    @Override
    default @NotNull MapCodec<A> withLifecycle(final @NotNull Lifecycle lifecycle) {
        Objects.requireNonNull(lifecycle, "lifecycle");
        return new MapCodec<>() {
            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return MapCodec.this.decode(input, ops).withLifecycle(lifecycle);
            }

            @Override
            public <T> @NotNull RecordBuilder<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
                return MapCodec.this.encode(input, ops, prefix).lifecycle(lifecycle);
            }

            @Override
            public String toString() {
                return MapCodec.this.toString();
            }
        };
    }

    /**
     * Converts this map codec in to its codec equivalent.
     *
     * @return A new map as standard codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> codec() {
        return new StandardCodec<>(this);
    }

    /**
     * The map codec equivalent of {@link Codec#xmap(Function, Function)}.
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     * @see Codec#xmap(Function, Function) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull MapCodec<B> xmap(final @NotNull Function<? super A, ? extends B> to, final @NotNull Function<? super B, ? extends A> from) {
        return of(comap(from), map(to), () -> this + "[xmapped]");
    }

    /**
     * The map codec equivalent of {@link Codec#flatXmap(Function, Function)}.
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     * @see Codec#flatXmap(Function, Function) The codec equivalent.
     */
    default <B> @NotNull MapCodec<B> flatXmap(final @NotNull Function<? super A, ? extends DataResult<? extends B>> to,
                                              final @NotNull Function<? super B, ? extends DataResult<? extends A>> from) {
        return of(flatComap(from), flatMap(to), () -> this + "[flatXmapped]");
    }

    /**
     * The map codec equivalent of {@link Codec#orElse(Object, Consumer)}.
     *
     * @param value The default value to use if the decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     * @see Codec#orElse(Object, Consumer) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElse(final @NotNull A value, final @NotNull Consumer<String> onError) {
        return orElse(value, CodecUtil.consumerToFunction(onError));
    }

    /**
     * The map codec equivalent of {@link Codec#orElse(Object, UnaryOperator)}.
     *
     * @param value The default value to use if the decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     * @see Codec#orElse(Object, UnaryOperator) The codec equivalent.
     */
    default @NotNull MapCodec<A> orElse(final @NotNull A value, final @NotNull UnaryOperator<String> onError) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(onError, "onError");
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                                    final @NotNull DataResult<A> result) {
                return DataResult.success(result.mapError(onError).result().orElse(value));
            }

            @Override
            public <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result) {
                return result.mapError(onError);
            }

            @Override
            public String toString() {
                return "OrElse[" + onError + " " + value + "]";
            }
        });
    }

    /**
     * The map codec equivalent of {@link Codec#orElseGet(Supplier, Consumer)}.
     *
     * @param value The default value supplier to get the result of if the
     *              decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     * @see Codec#orElseGet(Supplier, Consumer) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull Consumer<String> onError) {
        return orElseGet(value, CodecUtil.consumerToFunction(onError));
    }

    /**
     * The map codec equivalent of {@link Codec#orElseGet(Supplier, UnaryOperator)}.
     *
     * @param value The default value supplier to get the result of if the
     *              decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     * @see Codec#orElseGet(Supplier, UnaryOperator) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull UnaryOperator<String> onError) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(onError, "onError");
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                                    final @NotNull DataResult<A> result) {
                return DataResult.success(result.mapError(onError).result().orElseGet(value));
            }

            @Override
            public <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result) {
                return result.mapError(onError);
            }

            @Override
            public String toString() {
                return "OrElseGet[" + onError + " " + value.get() + "]";
            }
        });
    }

    /**
     * The map codec equivalent of {@link Codec#orElse(Object)}.
     *
     * @param value The default value to use if the decoder fails.
     * @return A new or else codec.
     * @see Codec#orElse(Object) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElse(final @NotNull A value) {
        Objects.requireNonNull(value, "value");
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                                    final @NotNull DataResult<A> result) {
                return DataResult.success(result.result().orElse(value));
            }

            @Override
            public <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result) {
                return result;
            }

            @Override
            public String toString() {
                return "OrElse[" + value + "]";
            }
        });
    }

    /**
     * The map codec equivalent of {@link Codec#orElseGet(Supplier)}.
     *
     * @param value The default value supplier to get the result of if the
     *              decoder fails.
     * @return A new or else codec.
     * @see Codec#orElseGet(Supplier) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value) {
        Objects.requireNonNull(value, "value");
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                                    final @NotNull DataResult<A> result) {
                return DataResult.success(result.result().orElseGet(value));
            }

            @Override
            public <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result) {
                return result;
            }

            @Override
            public String toString() {
                return "OrElseGet[" + value.get() + "]";
            }
        });
    }

    /**
     * Overrides the return value from the decoder, setting the partial result
     * to the result of applying the given value supplier.
     *
     * @param value The partial value supplier.
     * @return The resulting codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> withPartial(final @NotNull Supplier<A> value) {
        Objects.requireNonNull(value, "value");
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                                    final @NotNull DataResult<A> result) {
                return result.withPartial(value);
            }

            @Override
            public <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result) {
                return result;
            }

            @Override
            public String toString() {
                return "WithPartial[" + value.get() + "]";
            }
        });
    }

    /**
     * Maps the result from encoding/decoding using this codec by calling
     * functions on the given result function.
     *
     * @param function The function to map the results with.
     * @return A new codec that maps the results of this codec with the result
     *         function.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> mapResult(final @NotNull ResultFunction<A> function) {
        Objects.requireNonNull(function, "function");
        return new MapCodec<>() {
            @Override
            public <T> @NotNull DataResult<A> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return function.apply(input, ops, MapCodec.this.decode(input, ops));
            }

            @Override
            public <T> @NotNull RecordBuilder<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                        final @NotNull RecordBuilder<T> prefix) {
                return function.coApply(input, ops, MapCodec.this.encode(input, ops, prefix));
            }

            @Override
            public String toString() {
                return MapCodec.this + "[mapResult " + function + "]";
            }
        };
    }

    /**
     * A function that is used to modify the results of encoding/decoding with
     * a map codec.
     *
     * @param <A> The value type from the map codec.
     */
    interface ResultFunction<A> {

        /**
         * Returns the result that should be returned from the decoder.
         *
         * <p>This can be overridden to modify the result that the decode
         * function of a codec produces, especially to return a different
         * result when an error occurs.</p>
         *
         * @param input The input from the decoder.
         * @param ops The data operations from the decoder.
         * @param result The result that the decoder produced.
         * @param <T> The data type from the decoder.
         * @return The result.
         */
        <T> @NotNull DataResult<A> apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops, final @NotNull DataResult<A> result);

        /**
         * Returns the result that should be returned from the encoder.
         *
         * <p>This can be overridden to modify the result that the encoder
         * function of a codec produces, and catch errors that the encoder
         * may produce.</p>
         *
         * @param input The input from the encoder.
         * @param ops The data operations from the encoder.
         * @param result The result that the encoder produced, or the prefix
         *               that was provided if there was an error.
         * @param <T> The data type from the encoder.
         * @return The result. This is always non-null as it is expected that
         *         the given result will be returned if a different one cannot be.
         */
        <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result);
    }

    /**
     * A standard codec that wraps a map codec and encodes and decodes using
     * the wrapped map codec.
     *
     * <p>This is an easy bridge between the standard {@link Codec} and the map
     * codec, as they are different types.</p>
     *
     * @param codec The delegate map codec.
     * @param <A> The value type.
     */
    record StandardCodec<A>(@NotNull MapCodec<A> codec) implements Codec<A> {

        @SuppressWarnings("MissingJavadocMethod")
        public StandardCodec {
            Objects.requireNonNull(codec, "codec");
        }

        @Override
        public <T> @NotNull DataResult<Pair<A, T>> decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return codec.decode(input, ops).map(result -> Pair.of(result, input));
        }

        @Override
        public <T> @NotNull DataResult<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
            return codec.encode(input, ops, ops.mapBuilder()).build(prefix);
        }

        @Override
        public String toString() {
            return codec.toString();
        }
    }
}
