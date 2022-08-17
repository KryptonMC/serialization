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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
 * that will decode fields with specified names with the {@link #fieldOf(String)}
 * method.</p>
 *
 * @param <A> The value type.
 */
public interface Codec<A> extends Encoder<A>, Decoder<A> {

    @NotNull MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE));
    @NotNull Codec<Boolean> BOOLEAN = new PrimitiveCodec<>() {
        @Override
        public <T> @NotNull DataResult<Boolean> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
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
        public <T> @NotNull DataResult<Byte> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::byteValue);
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
        public <T> @NotNull DataResult<Short> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::shortValue);
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
        public <T> @NotNull DataResult<Integer> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::intValue);
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
        public <T> @NotNull DataResult<Long> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::longValue);
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
        public <T> @NotNull DataResult<Float> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::floatValue);
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
        public <T> @NotNull DataResult<Double> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return ops.getNumberValue(input).map(Number::doubleValue);
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
        public <T> @NotNull DataResult<String> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
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
        public <T> @NotNull DataResult<ByteBuffer> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
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
        public <T> @NotNull DataResult<IntStream> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
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
        public <T> @NotNull DataResult<LongStream> read(final @NotNull T input, final @NotNull DataOps<T> ops) {
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
    @NotNull Codec<Dynamic<?>> PASSTHROUGH = new Codec<>() {
        @Override
        public <T> @NotNull DataResult<Pair<Dynamic<?>, T>> decode(final T input, final @NotNull DataOps<T> ops) {
            return DataResult.success(Pair.of(new Dynamic<>(ops, input), ops.empty()));
        }

        @Override
        public <T> @NotNull DataResult<T> encode(final Dynamic<?> input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
            if (input.value() == input.ops().empty()) return DataResult.success(prefix, Lifecycle.experimental());
            final T converted = input.convert(ops).value();
            if (prefix == ops.empty()) return DataResult.success(converted, Lifecycle.experimental());
            return ops.getMap(converted).flatMap(map -> ops.mergeToMap(prefix, map)).result().map(DataResult::success).orElseGet(() ->
                    ops.getStream(converted)
                            .flatMap(stream -> ops.mergeToList(prefix, stream.collect(Collectors.toList()))).result().map(DataResult::success)
                            .orElseGet(() -> DataResult.error("Cannot merge prefix " + prefix + " and value " + converted + "!", prefix)));
        }

        @Override
        public String toString() {
            return "passthrough";
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
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return decoder.decode(input, ops);
            }

            @Override
            public <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
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
     * @param minInclusive The minimum value (inclusive).
     * @param maxInclusive The maximum value (inclusive).
     * @return A new int range codec.
     */
    static @NotNull Codec<Integer> intRange(final int minInclusive, final int maxInclusive) {
        final Function<Integer, DataResult<Integer>> checker = CodecUtil.checkRange(minInclusive, maxInclusive);
        return Codec.INT.flatXmap(checker, checker);
    }

    /**
     * Creates a new codec that performs bounds checking on input and output
     * values to ensure that they are between the given min and max values.
     *
     * <p>This codec will throw an {@link IllegalArgumentException} if the
     * input or output value provided is not within the required bounds.</p>
     *
     * @param minInclusive The minimum value (inclusive).
     * @param maxInclusive The maximum value (inclusive).
     * @return A new float range codec.
     */
    static @NotNull Codec<Float> floatRange(final float minInclusive, final float maxInclusive) {
        final Function<Float, DataResult<Float>> checker = CodecUtil.checkRange(minInclusive, maxInclusive);
        return Codec.FLOAT.flatXmap(checker, checker);
    }

    /**
     * Creates a new codec that performs bounds checking on input and output
     * values to ensure that they are between the given min and max values.
     *
     * <p>This codec will throw an {@link IllegalArgumentException} if the
     * input or output value provided is not within the required bounds.</p>
     *
     * @param minInclusive The minimum value (inclusive).
     * @param maxInclusive The maximum value (inclusive).
     * @return A new double range codec.
     */
    static @NotNull Codec<Double> doubleRange(final double minInclusive, final double maxInclusive) {
        final Function<Double, DataResult<Double>> checker = CodecUtil.checkRange(minInclusive, maxInclusive);
        return Codec.DOUBLE.flatXmap(checker, checker);
    }

    /**
     * Converts this codec to a new list codec that processes a list of
     * elements using this codec to process each value of the list.
     *
     * @return A new list codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<List<A>> listOf() {
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
     * Maps this codec to a new codec that applies the given to and from
     * functions to the input and output values respectively.
     *
     * <p>This is very similar to {@link #xmap(Function, Function)}, but the to
     * function in this method returns a {@link DataResult}, as it flat maps
     * the decoder.</p>
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Codec<B> comapFlatMap(final @NotNull Function<? super A, ? extends DataResult<? extends B>> to,
                                               final @NotNull Function<? super B, ? extends A> from) {
        return of(comap(from), flatMap(to), this + "[comapFlatMapped]");
    }

    /**
     * Maps this codec to a new codec that applies the given to and from
     * functions to the input and output values respectively.
     *
     * <p>This is very similar to {@link #xmap(Function, Function)}, but the
     * from function in this method returns a {@link DataResult}, as it flat
     * comaps the encoder.</p>
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Codec<B> flatComapMap(final @NotNull Function<? super A, ? extends B> to,
                                               final @NotNull Function<? super B, ? extends DataResult<? extends A>> from) {
        return of(flatComap(from), map(to), this + "[flatComapMapped]");
    }

    /**
     * Maps this codec to a new codec that applies the given to and from
     * functions to the input and output values respectively.
     *
     * <p>This is very similar to {@link #xmap(Function, Function)}, but both
     * functions in this method return a {@link DataResult}, as it flat maps
     * both the encoder and the decoder.</p>
     *
     * @param to The mapper to transform the output value.
     * @param from The mapper to transform the input value.
     * @param <B> The new codec type.
     * @return The mapped codec.
     */
    @ApiStatus.NonExtendable
    default <B> @NotNull Codec<B> flatXmap(final @NotNull Function<? super A, ? extends DataResult<? extends B>> to,
                                           final @NotNull Function<? super B, ? extends DataResult<? extends A>> from) {
        return of(flatComap(from), flatMap(to), this + "[flatXmapped]");
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
    default @NotNull MapCodec<A> fieldOf(final @NotNull String name) {
        return MapCodec.of(Encoder.super.fieldOf(name), Decoder.super.fieldOf(name), () -> "Field[" + name + ": " + this + "]");
    }

    /**
     * Creates a new map codec that optionally processes this codec as a value
     * of a field with a key of the given name.
     *
     * @param name The field name.
     * @return A new optional field map codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<Optional<A>> optionalFieldOf(final @NotNull String name) {
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
    default @NotNull MapCodec<A> optionalFieldOf(final @NotNull String name, final @NotNull A defaultValue) {
        return optionalField(name, this).xmap(
                value -> value.orElse(defaultValue),
                value -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value)
        );
    }

    /**
     * Creates a new map codec that optionally processes this codec as a value
     * of a field with a key of the given name, returning the default value if
     * the field is not present, and encoding nothing if the value is the
     * default value.
     *
     * @param name The field name.
     * @param defaultValue The default value.
     * @param defaultLifecycle The lifecycle of the default value.
     * @return A new optional field map codec.
     */
    @ApiStatus.NonExtendable
    default MapCodec<A> optionalFieldOf(final @NotNull String name, final @NotNull A defaultValue, final @NotNull Lifecycle defaultLifecycle) {
        return optionalFieldOf(name, Lifecycle.experimental(), defaultValue, defaultLifecycle);
    }

    /**
     * Creates a new map codec that optionally processes this codec as a value
     * of a field with a key of the given name, returning the default value if
     * the field is not present, and encoding nothing if the value is the
     * default value.
     *
     * @param name The field name.
     * @param fieldLifecycle The lifecycle of the field.
     * @param defaultValue The default value.
     * @param defaultLifecycle The lifecycle of the default value.
     * @return A new optional field map codec.
     */
    @ApiStatus.NonExtendable
    default MapCodec<A> optionalFieldOf(final @NotNull String name, final @NotNull Lifecycle fieldLifecycle, final @NotNull A defaultValue,
                                        final @NotNull Lifecycle defaultLifecycle) {
        return optionalField(name, this).stable().flatXmap(
                o -> o.map(v -> DataResult.success(v, fieldLifecycle)).orElse(DataResult.success(defaultValue, defaultLifecycle)),
                a -> Objects.equals(a, defaultValue) ?
                        DataResult.success(Optional.empty(), defaultLifecycle) :
                        DataResult.success(Optional.of(a), fieldLifecycle)
        );
    }

    @Override
    default @NotNull Codec<A> withLifecycle(final @NotNull Lifecycle lifecycle) {
        return new Codec<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return Codec.this.decode(input, ops).withLifecycle(lifecycle);
            }

            @Override
            public <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return Codec.this.encode(input, ops, prefix).withLifecycle(lifecycle);
            }

            @Override
            public String toString() {
                return Codec.this.toString();
            }
        };
    }

    /**
     * Creates a new codec that sets the lifecycle of the results to stable.
     *
     * @return A new stable codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> stable() {
        return withLifecycle(Lifecycle.stable());
    }

    /**
     * Creates a new codec that sets the lifecycle of the results to
     * deprecated, since the given version.
     *
     * @param since The version the codec is deprecated since.
     * @return A new deprecated codec.
     */
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> deprecated(final int since) {
        return withLifecycle(Lifecycle.deprecated(since));
    }

    @Override
    default @NotNull Codec<A> promotePartial(final @NotNull Consumer<String> onError) {
        return of(this, Decoder.super.promotePartial(onError));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull Codec<E> dispatch(final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatch("type", type, codec);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull Codec<E> dispatch(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap(typeKey, type, codec).codec();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull Codec<E> dispatchStable(final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return partialDispatch("type", e -> DataResult.success(type.apply(e), Lifecycle.stable()),
                a -> DataResult.success(codec.apply(a), Lifecycle.stable()));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull Codec<E> partialDispatch(
            final @NotNull String typeKey, final @NotNull Function<? super E, ? extends DataResult<? extends A>> type,
            final @NotNull Function<? super A, ? extends DataResult<? extends Codec<? extends E>>> codec) {
        return new KeyDispatchCodec<>(typeKey, this, type, codec).codec();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap("type", type, codec);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @ApiStatus.NonExtendable
    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return new KeyDispatchCodec<>(typeKey, this, type.andThen(DataResult::success), codec.andThen(DataResult::success));
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
    default @NotNull Codec<A> orElse(final @NotNull A value, final @NotNull Consumer<String> onError) {
        return orElse(value, CodecUtil.consumerToFunction(onError));
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
    default @NotNull Codec<A> orElse(final @NotNull A value, final @NotNull UnaryOperator<String> onError) {
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> apply(final T input, final @NotNull DataOps<T> ops,
                                                             final @NotNull DataResult<Pair<A, T>> result) {
                return DataResult.success(result.mapError(onError).result().orElseGet(() -> Pair.of(value, input)));
            }

            @Override
            public <T> @NotNull DataResult<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull DataResult<T> result) {
                return result.mapError(onError);
            }

            @Override
            public String toString() {
                return "OrElse[" + onError + " " + value + "]";
            }
        });
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
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull Consumer<String> onError) {
        return orElseGet(value, CodecUtil.consumerToFunction(onError));
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
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull UnaryOperator<String> onError) {
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> apply(final T input, final @NotNull DataOps<T> ops,
                                                             final @NotNull DataResult<Pair<A, T>> result) {
                return DataResult.success(result.mapError(onError).result().orElseGet(() -> Pair.of(value.get(), input)));
            }

            @Override
            public <T> @NotNull DataResult<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull DataResult<T> result) {
                return result.mapError(onError);
            }

            @Override
            public String toString() {
                return "OrElseGet[" + onError + " " + value.get() + "]";
            }
        });
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
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> apply(final T input, final @NotNull DataOps<T> ops,
                                                             final @NotNull DataResult<Pair<A, T>> result) {
                return DataResult.success(result.result().orElseGet(() -> Pair.of(value, input)));
            }

            @Override
            public <T> @NotNull DataResult<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull DataResult<T> result) {
                return result;
            }

            @Override
            public String toString() {
                return "OrElse[" + value + "]";
            }
        });
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
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull DataResult<Pair<A, T>> apply(final T input, final @NotNull DataOps<T> ops,
                                                             final @NotNull DataResult<Pair<A, T>> result) {
                return DataResult.success(result.result().orElseGet(() -> Pair.of(value.get(), input)));
            }

            @Override
            public <T> @NotNull DataResult<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull DataResult<T> result) {
                return result;
            }

            @Override
            public String toString() {
                return "OrElseGet[" + value.get() + "]";
            }
        });
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
            public <T> @NotNull DataResult<Pair<A, T>> decode(final T input, final @NotNull DataOps<T> ops) {
                return function.apply(input, ops, Codec.this.decode(input, ops));
            }

            @Override
            public <T> @NotNull DataResult<T> encode(final A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return function.coApply(input, ops, Codec.this.encode(input, ops, prefix));
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
         * @param result The result that the decoder produced.
         * @param <T> The data type from the decoder.
         * @return The result.
         */
        <T> @NotNull DataResult<Pair<A, T>> apply(final T input, final @NotNull DataOps<T> ops, final @NotNull DataResult<Pair<A, T>> result);

        /**
         * Returns the result that should be returned from the encoder.
         *
         * <p>This can be overridden to modify the result that the encoder
         * function of a codec produces, and catch errors that the encoder
         * may produce.</p>
         *
         * @param input The input from the encoder.
         * @param ops The data operations from the encoder.
         * @param result The result that the encoder produced.
         * @param <T> The data type from the encoder.
         * @return The result. This should always be the given result if a
         *         different result can/should not be provided.
         */
        <T> @NotNull DataResult<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull DataResult<T> result);
    }
}
