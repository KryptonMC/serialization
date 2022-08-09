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
import org.kryptonmc.serialization.codecs.PrimitiveCodec;
import org.kryptonmc.serialization.codecs.UnboundedMapCodec;
import org.kryptonmc.util.Either;
import org.kryptonmc.util.Pair;
import org.kryptonmc.util.Unit;

public interface Codec<A> extends Encoder<A>, Decoder<A> {

    @NotNull MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE));
    @NotNull PrimitiveCodec<Boolean> BOOLEAN = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Byte> BYTE = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Short> SHORT = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Integer> INT = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Long> LONG = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<String> STRING = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<ByteBuffer> BYTE_BUFFER = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<IntStream> INT_STREAM = new PrimitiveCodec<>() {
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
    @NotNull PrimitiveCodec<LongStream> LONG_STREAM = new PrimitiveCodec<>() {
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

    static <A> @NotNull Codec<A> of(final @NotNull Encoder<A> encoder, final @NotNull Decoder<A> decoder) {
        return of(encoder, decoder, "Codec[" + encoder + " " + decoder + "]");
    }

    static <A> @NotNull Codec<A> of(final @NotNull Encoder<A> encoder, final @NotNull Decoder<A> decoder, final @NotNull String name) {
        return new Codec<A>() {
            @Override
            public <T> @NotNull A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
                return decoder.decode(input, ops);
            }

            @Override
            public <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
                return encoder.encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    static <A> @NotNull Codec<A> unit(final @NotNull A defaultValue) {
        return MapCodec.unit(defaultValue).codec();
    }

    static <A> @NotNull Codec<A> unit(final @NotNull Supplier<A> defaultValue) {
        return MapCodec.unit(defaultValue).codec();
    }

    static <F, S> @NotNull Codec<Pair<F, S>> pair(final @NotNull Codec<F> first, final @NotNull Codec<S> second) {
        return new PairCodec<>(first, second);
    }

    static <L, R> @NotNull Codec<Either<L, R>> either(final @NotNull Codec<L> left, final @NotNull Codec<R> right) {
        return new EitherCodec<>(left, right);
    }

    static <E> @NotNull Codec<List<E>> list(final @NotNull Codec<E> elementCodec) {
        return new ListCodec<>(elementCodec);
    }

    static <K, V> @NotNull Codec<Map<K, V>> map(final @NotNull Codec<K> keyCodec, final @NotNull Codec<V> valueCodec) {
        return new UnboundedMapCodec<>(keyCodec, valueCodec);
    }

    static <F> @NotNull MapCodec<Optional<F>> optionalField(final @NotNull String name, final @NotNull Codec<F> elementCodec) {
        return new OptionalFieldCodec<>(name, elementCodec);
    }

    static @NotNull Codec<Integer> intRange(final int min, final int max) {
        final Function<Integer, Integer> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.INT.xmap(checker, checker);
    }

    static @NotNull Codec<Float> floatRange(final float min, final float max) {
        final Function<Float, Float> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.FLOAT.xmap(checker, checker);
    }

    static @NotNull Codec<Double> doubleRange(final double min, final double max) {
        final Function<Double, Double> checker = value -> {
            if (value < min || value > max) throw new IllegalArgumentException("Value " + value + " outside of range [" + min + ":" + max + "]");
            return value;
        };
        return Codec.DOUBLE.xmap(checker, checker);
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<List<A>> list() {
        return list(this);
    }

    @ApiStatus.NonExtendable
    default <B> @NotNull Codec<B> xmap(final @NotNull Function<? super A, ? extends B> to, final @NotNull Function<? super B, ? extends A> from) {
        return of(comap(from), map(to), this + "[xmapped]");
    }

    @Override
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> field(final @NotNull String name) {
        return MapCodec.of(Encoder.super.field(name), Decoder.super.field(name), () -> "Field[" + name + ": " + this + "]");
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<Optional<A>> optionalField(final @NotNull String name) {
        return optionalField(name, this);
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> optionalField(final @NotNull String name, final @NotNull A defaultValue) {
        return optionalField(name, this).xmap(
                value -> value.orElse(defaultValue),
                value -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value)
        );
    }

    default <E> @NotNull Codec<E> dispatch(final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatch("type", type, codec);
    }

    default <E> @NotNull Codec<E> dispatch(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                           final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap(typeKey, type, codec).codec();
    }

    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return dispatchMap("type", type, codec);
    }

    default <E> @NotNull MapCodec<E> dispatchMap(final @NotNull String typeKey, final @NotNull Function<? super E, ? extends A> type,
                                                 final @NotNull Function<? super A, ? extends Codec<? extends E>> codec) {
        return new KeyDispatchCodec<>(typeKey, this, type, codec);
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElse(final @NotNull A value, final @NotNull Consumer<Exception> onError) {
        return orElseGet(() -> value, onError, () -> "OrElse[" + onError + " " + value + "]");
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull Consumer<Exception> onError) {
        return orElseGet(value, onError, () -> "OrElseGet[" + onError + " " + value.get() + "]");
    }

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull Consumer<Exception> onError,
                                        final @NotNull Supplier<String> name) {
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull A apply(final @NotNull T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> {
                    onError.accept(error);
                    return value.get();
                });
            }

            @Override
            public <T> @NotNull T coApply(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T result,
                                          final @Nullable Exception exception) {
                if (exception != null) onError.accept(exception);
                return result;
            }
        });
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElse(final @NotNull A value) {
        return orElseGet(() -> value, () -> "OrElse[" + value + "]");
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value) {
        return orElseGet(value, () -> "OrElseGet[" + value.get() + "]");
    }

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default @NotNull Codec<A> orElseGet(final @NotNull Supplier<A> value, final @NotNull Supplier<String> name) {
        return mapResult(new ResultFunction<>() {
            @Override
            public <T> @NotNull A apply(final @NotNull T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> value.get());
            }

            @Override
            public <T> @NotNull T coApply(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T result,
                                          final @Nullable Exception exception) {
                return result;
            }
        });
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> mapResult(final ResultFunction<A> function) {
        return new Codec<>() {
            @Override
            public <T> @NotNull A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
                try {
                    return function.apply(input, ops, Either.left(Codec.this.decode(input, ops)));
                } catch (final Exception exception) {
                    return function.apply(input, ops, Either.right(exception));
                }
            }

            @Override
            public <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
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

    interface ResultFunction<A> {

        <T> @NotNull A apply(final @NotNull T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError);

        <T> @NotNull T coApply(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T result, final @Nullable Exception exception);
    }
}
