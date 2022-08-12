/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.codecs.RecordCodecBuilder;
import org.kryptonmc.util.Either;

/**
 * A specialisation of {@link Codec} that uses {@link MapLike} values as input
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
        return new MapCodec<>() {
            @Override
            public <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
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
     * The map codec equivalent of {@link Codec#orElse(Object, Consumer)}.
     *
     * @param value The default value to use if the decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     * @see Codec#orElse(Object, Consumer) The codec equivalent.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElse(final @NotNull A value, final @NotNull Consumer<Exception> onError) {
        return CodecUtil.orElseGet(this, () -> value, onError, () -> "OrElse[" + onError + " " + value + "]");
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
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull Consumer<Exception> onError) {
        return CodecUtil.orElseGet(this, value, onError, () -> "OrElseGet[" + onError + " " + value.get() + "]");
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
        return CodecUtil.orElseGet(this, () -> value, () -> "OrElse[" + value + "]");
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
        return CodecUtil.orElseGet(this, value, () -> "OrElseGet[" + value.get() + "]");
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
        return new MapCodec<>() {
            @Override
            public <T> A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                try {
                    return function.apply(input, ops, Either.left(MapCodec.this.decode(input, ops)));
                } catch (final Exception exception) {
                    return function.apply(input, ops, Either.right(exception));
                }
            }

            @Override
            public @NotNull <T> RecordBuilder<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                        final @NotNull RecordBuilder<T> prefix) {
                try {
                    return function.coApply(input, ops, MapCodec.this.encode(input, ops, prefix), null);
                } catch (final Exception exception) {
                    return function.coApply(input, ops, prefix, exception);
                }
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
         * @param resultOrError The result that the decoder produced, or the
         *                      error that occurred when attempting to decode
         *                      the result with the decoder.
         * @param <T> The data type from the decoder.
         * @return The result.
         */
        <T> A apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError);

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
         * @param exception The exception produced by the encoder, if one was
         *                  produced.
         * @param <T> The data type from the encoder.
         * @return The result. This is always non-null as it is expected that
         *         the given result will be returned if a different one cannot be.
         */
        <T> @NotNull RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result,
                                              final @Nullable Exception exception);
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

        @Override
        public <T> A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return codec.decode(input, ops);
        }

        @Override
        public <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
            return codec.encode(input, ops, ops.mapBuilder()).build(prefix);
        }

        @Override
        public String toString() {
            return codec.toString();
        }
    }
}
