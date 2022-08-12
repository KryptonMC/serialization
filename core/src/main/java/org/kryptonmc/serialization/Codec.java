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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/Codec.java
 */
package org.kryptonmc.serialization;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.serialization.codecs.EitherCodec;
import org.kryptonmc.serialization.codecs.KeyDispatchCodec;
import org.kryptonmc.serialization.codecs.ListCodec;
import org.kryptonmc.serialization.codecs.OptionalFieldCodec;
import org.kryptonmc.serialization.codecs.PairCodec;
import org.kryptonmc.serialization.codecs.UnboundedMapCodec;
import org.kryptonmc.util.Either;
import org.kryptonmc.util.Pair;
import org.kryptonmc.util.Unit;

/**
 * A two-way wrapper around some serialization logic. This is the core type in
 * this library, and is what is used to serialize and deserialize objects.
 *
 * <p>There are many pre-built codecs available from this interface, to avoid
 * having to create them all yourself, but the power in this library lies in
 * custom codecs.</p>
 *
 * <p>New codecs can be created in a functional manner with the {@link #of(Encoder, Decoder)}
 * method, which will create a new codec that's encode and decode methods
 * will delegate to the encoder and decoder's methods.</p>
 *
 * <p>What makes this library so powerful is that existing codecs can be easily
 * mapped to new ones. For example, {@link #xmap(Function, Function)} will
 * transform an existing codec to a new one by applying the given functions to
 * the input and output of the encoder/decoder of the input codec (the one the
 * method is called on). Also, codecs can be transformed in to field codecs
 * that will decode fields with specified names with the {@link #field(String)}
 * method.</p>
 *
 * @param <A> The value type.
 */
public interface Codec<A> extends Encoder<A>, Decoder<A> {

    @NotNull MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE));
    @NotNull Codec<Boolean> BOOLEAN = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Boolean read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getBooleanValue(input);
        }

        @Override
        public <T> @NotNull T write(final @NotNull Boolean value, final @NotNull DataOps<T> ops) {
            return ops.createBoolean(value);
        }

        @Override
        public String toString() {
            return "Boolean";
        }
    };
    @NotNull Codec<Byte> BYTE = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Byte read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).byteValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Byte value, final @NotNull DataOps<T> ops) {
            return ops.createByte(value);
        }

        @Override
        public String toString() {
            return "Byte";
        }
    };
    @NotNull Codec<Short> SHORT = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Short read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).shortValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Short value, final @NotNull DataOps<T> ops) {
            return ops.createShort(value);
        }

        @Override
        public String toString() {
            return "Short";
        }
    };
    @NotNull Codec<Integer> INT = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Integer read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).intValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Integer value, final @NotNull DataOps<T> ops) {
            return ops.createInt(value);
        }

        @Override
        public String toString() {
            return "Int";
        }
    };
    @NotNull Codec<Long> LONG = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Long read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).longValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Long value, final @NotNull DataOps<T> ops) {
            return ops.createLong(value);
        }

        @Override
        public String toString() {
            return "Long";
        }
    };
    @NotNull Codec<Float> FLOAT = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Float read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).floatValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Float value, final @NotNull DataOps<T> ops) {
            return ops.createFloat(value);
        }

        @Override
        public String toString() {
            return "Float";
        }
    };
    @NotNull Codec<Double> DOUBLE = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> Double read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).doubleValue();
        }

        @Override
        public <T> @NotNull T write(final @NotNull Double value, final @NotNull DataOps<T> ops) {
            return ops.createDouble(value);
        }

        @Override
        public String toString() {
            return "Double";
        }
    };
    @NotNull Codec<String> STRING = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> String read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getStringValue(input);
        }

        @Override
        public <T> @NotNull T write(final @NotNull String value, final @NotNull DataOps<T> ops) {
            return ops.createString(value);
        }

        @Override
        public String toString() {
            return "String";
        }
    };
    @NotNull Codec<ByteBuffer> BYTE_BUFFER = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> ByteBuffer read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getByteBuffer(input);
        }

        @Override
        public <T> @NotNull T write(final @NotNull ByteBuffer value, final @NotNull DataOps<T> ops) {
            return ops.createByteList(value);
        }

        @Override
        public String toString() {
            return "ByteBuffer";
        }
    };
    @NotNull Codec<IntStream> INT_STREAM = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> IntStream read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getIntStream(input);
        }

        @Override
        public <T> @NotNull T write(final @NotNull IntStream value, final @NotNull DataOps<T> ops) {
            return ops.createIntList(value);
        }

        @Override
        public String toString() {
            return "IntStream";
        }
    };
    @NotNull Codec<LongStream> LONG_STREAM = new PrimitiveCodec<>() {
        @Override
        public @NotNull <T> LongStream read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getLongStream(input);
        }

        @Override
        public <T> @NotNull T write(final @NotNull LongStream value, final @NotNull DataOps<T> ops) {
            return ops.createLongList(value);
        }

        @Override
        public String toString() {
            return "LongStream";
        }
    };

    /**
     * Creates a new codec that delegates the encoding and decoding to the
     * given encoder and decoder.
     *
     * @param encoder The encoder to encode with.
     * @param decoder The decoder to decode with.
     * @param <A> The result type.
     * @return A new codec.
     */
    static <A> @NotNull Codec<A> of(final @NotNull Encoder<A> encoder, final @NotNull Decoder<A> decoder) {
        return of(encoder, decoder, "Codec[" + encoder + " " + decoder + "]");
    }

    /**
     * Creates a new codec that delegates the encoding and decoding to the
     * given encoder and decoder, with the given name used for debugging
     * purposes.
     *
     * @param encoder The encoder to encode with.
     * @param decoder The decoder to decode with.
     * @param name The name of the codec.
     * @param <A> The result type.
     * @return A new codec.
     */
    static <A> @NotNull Codec<A> of(final @NotNull Encoder<A> encoder, final @NotNull Decoder<A> decoder, final @NotNull String name) {
        return new Codec<>() {
            @Override
            public <T> A decode(final T input, final @NotNull DataOps<T> ops) {
                return decoder.decode(input, ops);
            }

            @Override
            public <T> T encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return encoder.encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    /**
     * Creates a new codec that always returns the default value when decoding,
     * and always does nothing when encoding.
     *
     * @param defaultValue The value to always return when decoding.
     * @param <A> The result type.
     * @return A new unit codec.
     */
    static <A> @NotNull Codec<A> unit(final @NotNull A defaultValue) {
        return MapCodec.unit(defaultValue).codec();
    }

    /**
     * Creates a new codec that always returns the result of applying the
     * default value supplier when decoding, and always does nothing when
     * encoding.
     *
     * @param defaultValue The supplier to get the default value to return when
     *                     decoding from.
     * @param <A> The result type.
     * @return A new unit codec.
     */
    static <A> @NotNull Codec<A> unit(final @NotNull Supplier<A> defaultValue) {
        return MapCodec.unit(defaultValue).codec();
    }

    /**
     * Creates a new codec that encodes/decodes a pair of values using the
     * given first and second codec.
     *
     * @param first The first codec.
     * @param second The second codec.
     * @param <F> The first type.
     * @param <S> The second type.
     * @return A new pair codec.
     * @see PairCodec
     */
    static <F, S> @NotNull Codec<Pair<F, S>> pair(final @NotNull Codec<F> first, final @NotNull Codec<S> second) {
        return new PairCodec<>(first, second);
    }

    /**
     * Creates a new codec that attempts to encode/decode the left value, else
     * falls back to the right value.
     *
     * @param left The left codec.
     * @param right The right codec.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A new either codec.
     * @see EitherCodec
     */
    static <L, R> @NotNull Codec<Either<L, R>> either(final @NotNull Codec<L> left, final @NotNull Codec<R> right) {
        return new EitherCodec<>(left, right);
    }

    /**
     * Creates a new codec that encodes/decodes a list of values using the
     * element codec to process each element.
     *
     * @param elementCodec The element codec.
     * @param <E> The element type.
     * @return A new list codec.
     * @see ListCodec
     */
    @SuppressWarnings("AmbiguousMethodReference")
    static <E> @NotNull Codec<List<E>> list(final @NotNull Codec<E> elementCodec) {
        return new ListCodec<>(elementCodec);
    }

    /**
     * Creates a new codec that encodes/decodes a map of values using the key
     * codec to process the keys and the value codec to process the values.
     *
     * @param keyCodec The key codec.
     * @param valueCodec The value codec.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return A new map codec.
     */
    static <K, V> @NotNull Codec<Map<K, V>> map(final @NotNull Codec<K> keyCodec, final @NotNull Codec<V> valueCodec) {
        return new UnboundedMapCodec<>(keyCodec, valueCodec);
    }

    /**
     * Creates a new codec that optionally processes a field with the given
     * name using the given element codec to process the value of the field, if
     * found.
     *
     * @param name The field name.
     * @param elementCodec The field value codec.
     * @param <F> The field type.
     * @return A new optional field codec.
     */
    static <F> @NotNull MapCodec<Optional<F>> optionalField(final @NotNull String name, final @NotNull Codec<F> elementCodec) {
        return new OptionalFieldCodec<>(name, elementCodec);
    }

    /**
     * Creates a new codec that performs bounds checking on input and output
     * values to ensure that they are between the given min and max values.
     *
     * <p>This codec will throw an {@link IllegalArgumentException} if the
     * input or output value provided is not within the required bounds.</p>
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A new int range codec.
     */
    static @NotNull Codec<Integer> intRange(final int min, final int max) {
        final Function<Integer, Integer> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.INT.xmap(checker, checker);
    }

    /**
     * Creates a new codec that performs bounds checking on input and output
     * values to ensure that they are between the given min and max values.
     *
     * <p>This codec will throw an {@link IllegalArgumentException} if the
     * input or output value provided is not within the required bounds.</p>
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A new float range codec.
     */
    static @NotNull Codec<Float> floatRange(final float min, final float max) {
        final Function<Float, Float> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.FLOAT.xmap(checker, checker);
    }

    /**
     * Creates a new codec that performs bounds checking on input and output
     * values to ensure that they are between the given min and max values.
     *
     * <p>This codec will throw an {@link IllegalArgumentException} if the
     * input or output value provided is not within the required bounds.</p>
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A new double range codec.
     */
    static @NotNull Codec<Double> doubleRange(final double min, final double max) {
        final Function<Double, Double> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.DOUBLE.xmap(checker, checker);
    }

    /**
     * Converts this codec to a new list codec that processes a list of
     * elements using this codec to process each value of the list.
     *
     * @return A new list codec.
     */
    @SuppressWarnings("AmbiguousMethodReference")
    @ApiStatus.NonExtendable
    default @NotNull Codec<List<A>> list() {
        return list(this);
    }

    /**
     * Maps this codec to a new codec that applies the given to and from
     * functions to the input and output values respectively.
     *
     * <p>This is a very powerful function. It heavily reduces code
     * duplication, and makes it very easy to take one codec that does a lot of
     * the leg work and just transform the value from that complex codec to
     * another value.</p>
     *
     * <p>For example, this function is actually used internally for the
     * bounds checking codecs created by {@link #intRange(int, int)},
     * {@link #floatRange(float, float)}, and {@link #doubleRange(double, double)}.</p>
     *
     * <p>Specifically, the implementation of {@link #intRange(int, int)}, for
     * example, is as follows:</p>
     * <pre>
     * static @NotNull Codec&lt;Integer&gt; intRange(final int min, final int max) {
     *     final Function&lt;Integer, Integer&gt; checker = value -> {
     *         if (value &lt; min || value &gt; max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
     *         return value;
     *     };
     *     return Codec.INT.xmap(checker, checker);
     * }
     * </pre>
     *
     * <p>Without this function, this implementation would look more like so:</p>
     * <pre>
     * static @NotNull Codec&lt;Integer&gt; intRange(final int min, final int max) {
     *     return new PrimitiveCodec&lt;Integer&gt; {
     *         &#64;Override
     *         public &lt;T&gt; @NotNull Integer read(final @NotNull T input, final @NotNull DataOps&lt;T&gt; ops) {
     *             final int value = ops.getNumberValue(input).intValue();
     *             if (value &lt; min || value &gt; max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
     *             return value;
     *         }
     *
     *         &#64;Override
     *         public &lt;T&gt; @NotNull T write(final @NotNull Integer value, final @NotNull DataOps&lt;T&gt; ops) {
     *             if (value &lt; min || value &gt; max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
     *             return ops.createInt(value);
     *         }
     *     }
     * }
     * </pre>
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Codec<B> xmap(final @NotNull Function<? super A, ? extends B> to, final @NotNull Function<? super B, ? extends A> from) {
        return of(comap(from), map(to), this + "[xmapped]");
    }

    /**
     * Creates a new map codec that processes this codec as a value of a field
     * with a key of the given name.
     *
     * @param name The field name.
     * @return A new field map codec.
     */
    @Override
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> field(final @NotNull String name) {
        return MapCodec.of(Encoder.super.field(name), Decoder.super.field(name), () -> "Field[" + name + ": " + this + "]");
    }

    /**
     * Creates a new map codec that optionally processes this codec as a value
     * of a field with a key of the given name.
     *
     * @param name The field name.
     * @return A new optional field map codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<Optional<A>> optionalField(final @NotNull String name) {
        return optionalField(name, this);
    }

    /**
     * Creates a new map codec that optionally processes this codec as a value
     * of a field with a key of the given name, returning the default value if
     * the field is not present, and encoding nothing if the value is the
     * default value.
     *
     * @param name The field name.
     * @param defaultValue The default value.
     * @return A new optional field map codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> optionalField(final @NotNull String name, final @NotNull A defaultValue) {
        return optionalField(name, this).xmap(
                value -> value.orElse(defaultValue),
                value -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value)
        );
    }

    @SuppressWarnings("MissingJavadocMethod")
    default <E> @NotNull Codec<E> dispatch(final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatch("type", type, codec);
    }

    @SuppressWarnings("MissingJavadocMethod")
    default <E> @NotNull Codec<E> dispatch(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap(typeKey, type, codec).codec();
    }

    @SuppressWarnings("MissingJavadocMethod")
    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap("type", type, codec);
    }

    @SuppressWarnings("MissingJavadocMethod")
    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return new KeyDispatchCodec<>(typeKey, this, type, codec);
    }

    /**
     * Creates a new codec that attempts to encode/decode the input/output,
     * calling the given onError handler if an error occurs, and returning the
     * given value if the decoder fails.
     *
     * @param value The default value to use if the decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElse(final @NotNull A value, final @NotNull Consumer<Exception> onError) {
        return CodecUtil.orElseGet(this, () -> value, onError, () -> "OrElse[" + onError + " " + value + "]");
    }

    /**
     * Creates a new codec that attempts to encode/decode the input/output,
     * calling the given onError handler if an error occurs, and returning the
     * result of applying the given value supplier if the decoder fails.
     *
     * @param value The default value supplier to get the result of if the
     *              decoder fails.
     * @param onError The handler to call if an error occurs.
     * @return A new or else codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull Consumer<Exception> onError) {
        return CodecUtil.orElseGet(this, value, onError, () -> "OrElseGet[" + onError + " " + value.get() + "]");
    }

    /**
     * Creates a new codec that attempts to encode/decode the input/output,
     * returning the given value if the decoder fails.
     *
     * @param value The default value to use if the decoder fails.
     * @return A new or else codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElse(final @NotNull A value) {
        return CodecUtil.orElseGet(this, () -> value, () -> "OrElse[" + value + "]");
    }

    /**
     * Creates a new codec that attempts to encode/decode the input/output,
     * returning the result of applying the given value supplier if the
     * decoder fails.
     *
     * @param value The default value supplier to get the result of if the
     *              decoder fails.
     * @return A new or else codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value) {
        return CodecUtil.orElseGet(this, value, () -> "OrElseGet[" + value.get() + "]");
    }

    /**
     * Maps the result of encoding/decoding the input/output of this codec
     * using the given function.
     *
     * @param function The function to map the results with.
     * @return A new mapped codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> mapResult(final ResultFunction<A> function) {
        return new Codec<>() {
            @Override
            public <T> A decode(final T input, final @NotNull DataOps<T> ops) {
                try {
                    return function.apply(input, ops, Either.left(Codec.this.decode(input, ops)));
                } catch (final Exception exception) {
                    return function.apply(input, ops, Either.right(exception));
                }
            }

            @Override
            public <T> T encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                try {
                    return function.coApply(input, ops, Codec.this.encode(input, ops, prefix), null);
                } catch (final Exception exception) {
                    return function.coApply(input, ops, prefix, exception);
                }
            }

            @Override
            public String toString() {
                return Codec.this + "[mapResult " + function + "]";
            }
        };
    }

    /**
     * A function that is used to modify the results of encoding/decoding with
     * a codec.
     *
     * @param <A> The value type from the codec.
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
        <T> A apply(final T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError);

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
         * @return The result. This should always be the given result if a
         *         different result can/should not be provided.
         */
        <T> T coApply(final A input, final @NotNull DataOps<T> ops, final T result, final @Nullable Exception exception);
    }
}
