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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/RecordCodecBuilder.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Decoder;
import org.kryptonmc.serialization.Encoder;
import org.kryptonmc.serialization.Lifecycle;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapDecoder;
import org.kryptonmc.serialization.MapEncoder;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.function.Function3;
import org.kryptonmc.util.function.Function4;
import org.kryptonmc.util.functional.App;
import org.kryptonmc.util.functional.Applicative;
import org.kryptonmc.util.functional.K1;

/**
 * A builder for building codecs for complex types that are composed of other
 * codecs.
 *
 * <p>This is very powerful, as it allows easy composition of multiple codecs
 * for complex types, without needing to write any imperative logic. These are
 * written in a general style, as follows:</p>
 * <pre>
 * public record Person(String name, int age) {
 *
 *     public static final Codec&lt;MyComplexType&gt; CODEC = RecordCodecBuilder.create(instance -> {
 *         instance.group(
 *             Codec.STRING.field("name").getting(Person::name),
 *             Codec.INT.field("age").getting(Person::age)
 *         ).apply(instance, Person::new);
 *     });
 * }
 * </pre>
 *
 * <p>For a person with a name of "Joe Bloggs" and an age of 21, this would
 * produce the following result, shown in JSON for demonstration:</p>
 * <pre>
 * {
 *     "name": "Joe Bloggs",
 *     "age": 21
 * }
 * </pre>
 *
 * <p>The advantage to this approach over a more conventional imperative-style
 * approach is the apparent lack of code repetition. In an imperative-style
 * codec, encoder and decoder logic is often duplicated, when all we want is
 * to describe how the abstract data transforms to and from our complex type.</p>
 *
 * <p>This more functional-style approach allows us to just describe how to
 * transform our complex type to data and how to transform data to our complex
 * type, and does not require us to specify exactly how the serialization is
 * done.</p>
 *
 * @param <O> The input type.
 * @param <F> The function type.
 */
public final class RecordCodecBuilder<O, F> implements App<RecordCodecBuilder.Mu<O>, F> {

    /**
     * Creates a new instance for the record codec builder applicative.
     *
     * @param <O> The input type.
     * @return A new instance.
     */
    public static <O> @NotNull Instance<O> instance() {
        return new Instance<>();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static <O, F> @NotNull RecordCodecBuilder<O, F> of(final @NotNull Function<O, F> getter, final @NotNull MapCodec<F> codec) {
        return new RecordCodecBuilder<>(getter, o -> codec, codec);
    }

    /**
     * Creates a new record codec builder that uses the given instance as the
     * result of decoding the data to the complex type, and does nothing when
     * encoding objects to data.
     *
     * @param instance The instance to use.
     * @param <O> The input type.
     * @param <F> The function type.
     * @return The record codec builder.
     */
    public static <O, F> @NotNull RecordCodecBuilder<O, F> point(final @NotNull F instance) {
        return new RecordCodecBuilder<>(o -> instance, o -> Encoder.empty(), Decoder.unit(instance));
    }

    /**
     * Creates a new record codec builder that uses the given instance as the
     * result of decoding the data to the complex type, and does nothing when
     * encoding objects to data, with the lifecycle of
     * {@link Lifecycle#stable()}.
     *
     * @param instance The instance to use.
     * @param <O> The input type.
     * @param <F> The function type.
     * @return The record codec builder.
     */
    public static <O, F> @NotNull RecordCodecBuilder<O, F> stable(final @NotNull F instance) {
        return point(instance, Lifecycle.stable());
    }

    /**
     * Creates a new record codec builder that uses the given instance as the
     * result of decoding the data to the complex type, and does nothing when
     * encoding objects to data, with the lifecycle of
     * {@link Lifecycle#deprecated(int)}.
     *
     * @param instance The instance to use.
     * @param since The since version for the deprecated lifecycle.
     * @param <O> The input type.
     * @param <F> The function type.
     * @return The record codec builder.
     */
    public static <O, F> @NotNull RecordCodecBuilder<O, F> deprecated(final @NotNull F instance, final int since) {
        return point(instance, Lifecycle.deprecated(since));
    }

    /**
     * Creates a new record codec builder that uses the given instance as the
     * result of decoding the data to the complex type, and does nothing when
     * encoding objects to data, with the given lifecycle.
     *
     * @param instance The instance to use.
     * @param lifecycle The lifecycle.
     * @param <O> The input type.
     * @param <F> The function type.
     * @return The record codec builder.
     */
    public static <O, F> @NotNull RecordCodecBuilder<O, F> point(final @NotNull F instance, final @NotNull Lifecycle lifecycle) {
        return new RecordCodecBuilder<>(o -> instance, o -> Encoder.<F>empty().withLifecycle(lifecycle),
                Decoder.unit(instance).withLifecycle(lifecycle));
    }

    /**
     * Creates a new codec for serializing a complex type by applying the given
     * builder function to a new instance of the record codec builder
     * applicative.
     *
     * @param builder The builder to apply.
     * @param <O> The complex type.
     * @return The resulting codec.
     */
    public static <O> @NotNull Codec<O> create(final @NotNull Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance())).codec();
    }

    /**
     * Creates a new map codec for serializing a complex type by applying the
     * given builder function to a new instance of the record codec builder
     * applicative.
     *
     * @param builder The builder to apply.
     * @param <O> The complex type.
     * @return The resulting map codec.
     */
    public static <O> @NotNull MapCodec<O> createMap(final @NotNull Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance()));
    }

    private static <O> @NotNull MapCodec<O> build(final @NotNull App<Mu<O>, O> builderBox) {
        final var builder = unbox(builderBox);
        return new MapCodec<>() {
            @Override
            public <T> @NotNull DataResult<O> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return builder.decoder.decode(input, ops);
            }

            @Override
            public <T> @NotNull RecordBuilder<T> encode(final O input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
                return builder.encoder.apply(input).encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return "RecordCodec[" + builder.decoder + "]";
            }
        };
    }

    private static <O, F> @NotNull RecordCodecBuilder<O, F> unbox(final @NotNull App<Mu<O>, F> box) {
        return (RecordCodecBuilder<O, F>) box;
    }

    private final Function<O, F> getter;
    private final Function<O, MapEncoder<F>> encoder;
    private final MapDecoder<F> decoder;

    private RecordCodecBuilder(final @NotNull Function<O, F> getter, final @NotNull Function<O, MapEncoder<F>> encoder,
                               final @NotNull MapDecoder<F> decoder) {
        this.getter = getter;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    /**
     * A mu for the record codec builder applicative.
     *
     * @param <O> The target complex type for the record codec builder.
     */
    public static final class Mu<O> implements K1 {

        private Mu() {
        }
    }

    /**
     * An instance for the record codec builder applicative.
     *
     * @param <O> The input type for the record codec builder.
     */
    public static final class Instance<O> implements Applicative<Mu<O>, Instance.Mu<O>> {

        private Instance() {
        }

        @Override
        public <A> @NotNull App<RecordCodecBuilder.Mu<O>, A> point(final @NotNull A a) {
            return RecordCodecBuilder.point(a);
        }

        @Override
        public <A, R> @NotNull Function<App<RecordCodecBuilder.Mu<O>, A>, App<RecordCodecBuilder.Mu<O>, R>> lift1(
                final @NotNull App<RecordCodecBuilder.Mu<O>, Function<A, R>> function) {
            return fa -> {
                final var f = unbox(function);
                final var a = unbox(fa);
                return new RecordCodecBuilder<>(
                        o -> f.getter.apply(o).apply(a.getter.apply(o)),
                        o -> {
                            final var fEncoder = f.encoder.apply(o);
                            final var aEncoder = a.encoder.apply(o);
                            final var aFromO = a.getter.apply(o);
                            return new MapEncoder<R>() {
                                @Override
                                public <T> @NotNull RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
                                                                            final @NotNull RecordBuilder<T> prefix) {
                                    aEncoder.encode(aFromO, ops, prefix);
                                    fEncoder.encode(a1 -> input, ops, prefix);
                                    return prefix;
                                }

                                @Override
                                public String toString() {
                                    return fEncoder + " * " + aEncoder;
                                }
                            };
                        },
                        new MapDecoder<>() {
                            @Override
                            public <T> @NotNull DataResult<R> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                                return a.decoder.decode(input, ops).flatMap(ar -> f.decoder.decode(input, ops).map(fr -> fr.apply(ar)));
                            }

                            @Override
                            public String toString() {
                                return f.decoder + " * " + a.decoder;
                            }
                        }
                );
            };
        }

        @SuppressWarnings("Convert2Diamond")
        @Override
        public <A, B, R> @NotNull App<RecordCodecBuilder.Mu<O>, R> ap2(final @NotNull App<RecordCodecBuilder.Mu<O>, BiFunction<A, B, R>> function,
                                                                       final @NotNull App<RecordCodecBuilder.Mu<O>, A> a,
                                                                       final @NotNull App<RecordCodecBuilder.Mu<O>, B> b) {
            final var f = unbox(function);
            final var fa = unbox(a);
            final var fb = unbox(b);
            return new RecordCodecBuilder<>(
                    o -> f.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o)),
                    o -> {
                        final var fEncoder = f.encoder.apply(o);
                        final var aEncoder = fa.encoder.apply(o);
                        final var aFromO = fa.getter.apply(o);
                        final var bEncoder = fb.encoder.apply(o);
                        final var bFromO = fb.getter.apply(o);
                        // If we make it a diamond here, it fails to compile for some reason.
                        return new MapEncoder<R>() {
                            @Override
                            public <T> @NotNull RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
                                                                        final @NotNull RecordBuilder<T> prefix) {
                                aEncoder.encode(aFromO, ops, prefix);
                                bEncoder.encode(bFromO, ops, prefix);
                                fEncoder.encode((a1, b1) -> input, ops, prefix);
                                return prefix;
                            }

                            @Override
                            public String toString() {
                                return fEncoder + " * " + aEncoder + " * " + bEncoder;
                            }
                        };
                    },
                    new MapDecoder<>() {
                        @Override
                        public <T> @NotNull DataResult<R> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return unboxResult(DataResult.instance().ap2(
                                    f.decoder.decode(input, ops),
                                    fa.decoder.decode(input, ops),
                                    fb.decoder.decode(input, ops)
                            ));
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder;
                        }
                    }
            );
        }

        @SuppressWarnings("Convert2Diamond")
        @Override
        public <A, B, C, R> @NotNull App<RecordCodecBuilder.Mu<O>, R> ap3(
                final @NotNull App<RecordCodecBuilder.Mu<O>, Function3<A, B, C, R>> function, final @NotNull App<RecordCodecBuilder.Mu<O>, A> a,
                final @NotNull App<RecordCodecBuilder.Mu<O>, B> b, final @NotNull App<RecordCodecBuilder.Mu<O>, C> c) {
            final var f = unbox(function);
            final var fa = unbox(a);
            final var fb = unbox(b);
            final var fc = unbox(c);
            return new RecordCodecBuilder<>(
                    o -> f.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o), fc.getter.apply(o)),
                    o -> {
                        final var fEncoder = f.encoder.apply(o);
                        final var aEncoder = fa.encoder.apply(o);
                        final var aFromO = fa.getter.apply(o);
                        final var bEncoder = fb.encoder.apply(o);
                        final var bFromO = fb.getter.apply(o);
                        final var cEncoder = fc.encoder.apply(o);
                        final var cFromO = fc.getter.apply(o);
                        // If we make it a diamond here, it fails to compile for some reason.
                        return new MapEncoder<R>() {
                            @Override
                            public <T> @NotNull RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
                                                                        final @NotNull RecordBuilder<T> prefix) {
                                aEncoder.encode(aFromO, ops, prefix);
                                bEncoder.encode(bFromO, ops, prefix);
                                cEncoder.encode(cFromO, ops, prefix);
                                fEncoder.encode((a1, b1, c1) -> input, ops, prefix);
                                return prefix;
                            }

                            @Override
                            public String toString() {
                                return fEncoder + " * " + aEncoder + " * " + bEncoder + " * " + cEncoder;
                            }
                        };
                    },
                    new MapDecoder<>() {
                        @Override
                        public <T> @NotNull DataResult<R> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return unboxResult(DataResult.instance().ap3(
                                    f.decoder.decode(input, ops),
                                    fa.decoder.decode(input, ops),
                                    fb.decoder.decode(input, ops),
                                    fc.decoder.decode(input, ops)
                            ));
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder + " * " + fc.decoder;
                        }
                    }
            );
        }

        @SuppressWarnings("Convert2Diamond")
        @Override
        public <A, B, C, D, R> @NotNull App<RecordCodecBuilder.Mu<O>, R> ap4(
                final @NotNull App<RecordCodecBuilder.Mu<O>, Function4<A, B, C, D, R>> function, final @NotNull App<RecordCodecBuilder.Mu<O>, A> a,
                final @NotNull App<RecordCodecBuilder.Mu<O>, B> b, final @NotNull App<RecordCodecBuilder.Mu<O>, C> c,
                final @NotNull App<RecordCodecBuilder.Mu<O>, D> d) {
            final var f = unbox(function);
            final var fa = unbox(a);
            final var fb = unbox(b);
            final var fc = unbox(c);
            final var fd = unbox(d);
            return new RecordCodecBuilder<>(
                    o -> f.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o), fc.getter.apply(o), fd.getter.apply(o)),
                    o -> {
                        final var fEncoder = f.encoder.apply(o);
                        final var aEncoder = fa.encoder.apply(o);
                        final var aFromO = fa.getter.apply(o);
                        final var bEncoder = fb.encoder.apply(o);
                        final var bFromO = fb.getter.apply(o);
                        final var cEncoder = fc.encoder.apply(o);
                        final var cFromO = fc.getter.apply(o);
                        final var dEncoder = fd.encoder.apply(o);
                        final var dFromO = fd.getter.apply(o);
                        // If we make it a diamond here, it fails to compile for some reason.
                        return new MapEncoder<R>() {
                            @Override
                            public <T> @NotNull RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
                                                                        final @NotNull RecordBuilder<T> prefix) {
                                aEncoder.encode(aFromO, ops, prefix);
                                bEncoder.encode(bFromO, ops, prefix);
                                cEncoder.encode(cFromO, ops, prefix);
                                dEncoder.encode(dFromO, ops, prefix);
                                fEncoder.encode((a1, b1, c1, d1) -> input, ops, prefix);
                                return prefix;
                            }

                            @Override
                            public String toString() {
                                return fEncoder + " * " + aEncoder + " * " + bEncoder + " * " + cEncoder + " * " + dEncoder;
                            }
                        };
                    },
                    new MapDecoder<>() {
                        @Override
                        public <T> @NotNull DataResult<R> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return unboxResult(DataResult.instance().ap4(
                                    f.decoder.decode(input, ops),
                                    fa.decoder.decode(input, ops),
                                    fb.decoder.decode(input, ops),
                                    fc.decoder.decode(input, ops),
                                    fd.decoder.decode(input, ops)
                            ));
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder + " * " + fc.decoder + " * " + fd.decoder;
                        }
                    }
            );
        }

        @SuppressWarnings("Convert2Diamond")
        @Override
        public <T, R> @NotNull App<RecordCodecBuilder.Mu<O>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                                    final @NotNull App<RecordCodecBuilder.Mu<O>, T> argument) {
            final var unbox = unbox(argument);
            final var getter = unbox.getter;
            return new RecordCodecBuilder<>(
                    getter.andThen(function),
                    // If we make it a diamond here, it fails to compile for some reason.
                    o -> new MapEncoder<R>() {

                        private final MapEncoder<T> encoder = unbox.encoder.apply(o);

                        @Override
                        public @NotNull <U> RecordBuilder<U> encode(final R input, final @NotNull DataOps<U> ops,
                                                                    final @NotNull RecordBuilder<U> prefix) {
                            return encoder.encode(getter.apply(o), ops, prefix);
                        }

                        @Override
                        public String toString() {
                            return encoder + "[mapped]";
                        }
                    },
                    unbox.decoder.map(function)
            );
        }

        private static <R> @NotNull DataResult<R> unboxResult(final @NotNull App<DataResult.Mu, R> box) {
            return (DataResult<R>) box;
        }

        private static final class Mu<O> implements Applicative.Mu {
        }
    }
}
