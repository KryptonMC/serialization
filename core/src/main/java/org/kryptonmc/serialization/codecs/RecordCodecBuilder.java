/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.Decoder;
import org.kryptonmc.serialization.Encoder;
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

public final class RecordCodecBuilder<O, F> implements App<RecordCodecBuilder.Mu<O>, F> {

    public static <O, F> @NotNull RecordCodecBuilder<O, F> unbox(final @NotNull App<Mu<O>, F> box) {
        return (RecordCodecBuilder<O, F>) box;
    }

    public static <O> @NotNull Instance<O> instance() {
        return new Instance<>();
    }

    public static <O, F> @NotNull RecordCodecBuilder<O, F> of(final @NotNull Function<O, F> getter, final @NotNull String name,
                                                              final @NotNull Codec<F> fieldCodec) {
        return of(getter, fieldCodec.field(name));
    }

    public static <O, F> @NotNull RecordCodecBuilder<O, F> of(final @NotNull Function<O, F> getter, final @NotNull MapCodec<F> codec) {
        return new RecordCodecBuilder<>(getter, o -> codec, codec);
    }

    public static <O, F> @NotNull RecordCodecBuilder<O, F> point(final @NotNull F instance) {
        return new RecordCodecBuilder<>(o -> instance, o -> Encoder.empty(), Decoder.unit(instance));
    }

    public static <O> @NotNull Codec<O> create(final @NotNull Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance())).codec();
    }

    public static <O> @NotNull MapCodec<O> createMap(final @NotNull Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance()));
    }

    private static <O> @NotNull MapCodec<O> build(final @NotNull App<Mu<O>, O> builderBox) {
        final var builder = unbox(builderBox);
        return new MapCodec<>() {
            @Override
            public <T> O decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return builder.decoder.decode(input, ops);
            }

            @Override
            public @NotNull <T> RecordBuilder<T> encode(final O input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
                return builder.encoder.apply(input).encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return "RecordCodec[" + builder.decoder + "]";
            }
        };
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

    public static final class Mu<O> implements K1 {}

    public static final class Instance<O> implements Applicative<Mu<O>, Instance.Mu<O>> {

        @Override
        public @NotNull <A> App<RecordCodecBuilder.Mu<O>, A> point(final @NotNull A a) {
            return RecordCodecBuilder.point(a);
        }

        @Override
        public @NotNull <A, R> Function<App<RecordCodecBuilder.Mu<O>, A>, App<RecordCodecBuilder.Mu<O>, R>> lift1(
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
                                public @NotNull <T> RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
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
                            public <T> R decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                                return f.decoder.decode(input, ops).apply(a.decoder.decode(input, ops));
                            }

                            @Override
                            public String toString() {
                                return f.decoder + " * " + a.decoder;
                            }
                        }
                );
            };
        }

        @Override
        public @NotNull <A, B, R> App<RecordCodecBuilder.Mu<O>, R> ap2(final @NotNull App<RecordCodecBuilder.Mu<O>, BiFunction<A, B, R>> function,
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
                        //noinspection Convert2Diamond
                        return new MapEncoder<R>() {
                            @Override
                            public @NotNull <T> RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
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
                        public <T> R decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return f.decoder.decode(input, ops).apply(fa.decoder.decode(input, ops), fb.decoder.decode(input, ops));
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder;
                        }
                    }
            );
        }

        @Override
        public @NotNull <A, B, C, R> App<RecordCodecBuilder.Mu<O>, R> ap3(
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
                        //noinspection Convert2Diamond
                        return new MapEncoder<R>() {
                            @Override
                            public @NotNull <T> RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
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
                        public <T> R decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return f.decoder.decode(input, ops).apply(
                                    fa.decoder.decode(input, ops),
                                    fb.decoder.decode(input, ops),
                                    fc.decoder.decode(input, ops)
                            );
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder + " * " + fc.decoder;
                        }
                    }
            );
        }

        @Override
        public @NotNull <A, B, C, D, R> App<RecordCodecBuilder.Mu<O>, R> ap4(
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
                        //noinspection Convert2Diamond
                        return new MapEncoder<R>() {
                            @Override
                            public @NotNull <T> RecordBuilder<T> encode(final R input, final @NotNull DataOps<T> ops,
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
                        public <T> R decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                            return f.decoder.decode(input, ops).apply(
                                    fa.decoder.decode(input, ops),
                                    fb.decoder.decode(input, ops),
                                    fc.decoder.decode(input, ops),
                                    fd.decoder.decode(input, ops)
                            );
                        }

                        @Override
                        public String toString() {
                            return f.decoder + " * " + fa.decoder + " * " + fb.decoder + " * " + fc.decoder + " * " + fd.decoder;
                        }
                    }
            );
        }

        @Override
        public @NotNull <T, R> App<RecordCodecBuilder.Mu<O>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                                    final @NotNull App<RecordCodecBuilder.Mu<O>, T> argument) {
            final var unbox = unbox(argument);
            final var getter = unbox.getter;
            //noinspection Convert2Diamond
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

        private static final class Mu<O> implements Applicative.Mu {}
    }
}
