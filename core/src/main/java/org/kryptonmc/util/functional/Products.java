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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/Products.java
 */
package org.kryptonmc.util.functional;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.function.Function10;
import org.kryptonmc.util.function.Function11;
import org.kryptonmc.util.function.Function12;
import org.kryptonmc.util.function.Function13;
import org.kryptonmc.util.function.Function14;
import org.kryptonmc.util.function.Function15;
import org.kryptonmc.util.function.Function16;
import org.kryptonmc.util.function.Function3;
import org.kryptonmc.util.function.Function4;
import org.kryptonmc.util.function.Function5;
import org.kryptonmc.util.function.Function6;
import org.kryptonmc.util.function.Function7;
import org.kryptonmc.util.function.Function8;
import org.kryptonmc.util.function.Function9;

public interface Products {

    record P1<FN extends K1, A>(@NotNull App<FN, A> a) {

        public <B> @NotNull P2<FN, A, B> and(final @NotNull App<FN, B> b) {
            return new P2<>(a, b);
        }

        public <B, C> @NotNull P3<FN, A, B, C> and(final @NotNull App<FN, B> b, final @NotNull App<FN, C> c) {
            return new P3<>(a, b, c);
        }

        public <B, C, D> @NotNull P4<FN, A, B, C, D> and(final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d) {
            return new P4<>(a, b, c, d);
        }

        public <B, C, D, E> @NotNull P5<FN, A, B, C, D, E> and(final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
                                                               final @NotNull App<FN, E> e) {
            return new P5<>(a, b, c, d, e);
        }

        public <B, C, D, E, F> @NotNull P6<FN, A, B, C, D, E, F> and(
                final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
                final @NotNull App<FN, F> f) {
            return new P6<>(a, b, c, d, e, f);
        }

        public <B, C, D, E, F, G> @NotNull P7<FN, A, B, C, D, E, F, G> and(
                final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
                final @NotNull App<FN, F> f, final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <B, C, D, E, F, G, H> @NotNull P8<FN, A, B, C, D, E, F, G, H> and(
                final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
                final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function<A, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull App<FN, Function<A, R>> function) {
            return instance.ap(function, a);
        }
    }

    record P2<FN extends K1, A, B>(@NotNull App<FN, A> a, @NotNull App<FN, B> b) {

        public <C> @NotNull P3<FN, A, B, C> and(final @NotNull App<FN, C> c) {
            return new P3<>(a, b, c);
        }

        public <C, D> @NotNull P4<FN, A, B, C, D> and(final @NotNull App<FN, C> c, final @NotNull App<FN, D> d) {
            return new P4<>(a, b, c, d);
        }

        public <C, D, E> @NotNull P5<FN, A, B, C, D, E> and(final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e) {
            return new P5<>(a, b, c, d, e);
        }

        public <C, D, E, F> @NotNull P6<FN, A, B, C, D, E, F> and(final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
                                                                  final @NotNull App<FN, E> e, final @NotNull App<FN, F> f) {
            return new P6<>(a, b, c, d, e, f);
        }

        public <C, D, E, F, G> @NotNull P7<FN, A, B, C, D, E, F, G> and(
                final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
                final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <C, D, E, F, G, H> @NotNull P8<FN, A, B, C, D, E, F, G, H> and(
                final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
                final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull BiFunction<A, B, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull App<FN, BiFunction<A, B, R>> function) {
            return instance.ap2(function, a, b);
        }
    }

    record P3<FN extends K1, A, B, C>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c) {

        public <D> @NotNull P4<FN, A, B, C, D> and(final @NotNull App<FN, D> d) {
            return new P4<>(a, b, c, d);
        }

        public <D, E> @NotNull P5<FN, A, B, C, D, E> and(final @NotNull App<FN, D> d, final @NotNull App<FN, E> e) {
            return new P5<>(a, b, c, d, e);
        }

        public <D, E, F> @NotNull P6<FN, A, B, C, D, E, F> and(final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
                                                               final @NotNull App<FN, F> f) {
            return new P6<>(a, b, c, d, e, f);
        }

        public <D, E, F, G> @NotNull P7<FN, A, B, C, D, E, F, G> and(final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
                                                                     final @NotNull App<FN, F> f, final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <D, E, F, G, H> @NotNull P8<FN, A, B, C, D, E, F, G, H> and(
                final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g,
                final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function3<A, B, C, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull App<FN, Function3<A, B, C, R>> function) {
            return instance.ap3(function, a, b, c);
        }
    }

    record P4<FN extends K1, A, B, C, D>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d) {

        public <E> @NotNull P5<FN, A, B, C, D, E> and(final @NotNull App<FN, E> e) {
            return new P5<>(a, b, c, d, e);
        }

        public <E, F> @NotNull P6<FN, A, B, C, D, E, F> and(final @NotNull App<FN, E> e, final @NotNull App<FN, F> f) {
            return new P6<>(a, b, c, d, e, f);
        }

        public <E, F, G> @NotNull P7<FN, A, B, C, D, E, F, G> and(final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
                                                                  final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <E, F, G, H> @NotNull P8<FN, A, B, C, D, E, F, G, H> and(final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
                                                                        final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function4<A, B, C, D, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull App<FN, Function4<A, B, C, D, R>> function) {
            return instance.ap4(function, a, b, c, d);
        }
    }

    record P5<FN extends K1, A, B, C, D, E>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                            @NotNull App<FN, E> e) {

        public <F> @NotNull P6<FN, A, B, C, D, E, F> and(final @NotNull App<FN, F> f) {
            return new P6<>(a, b, c, d, e, f);
        }

        public <F, G> @NotNull P7<FN, A, B, C, D, E, F, G> and(final @NotNull App<FN, F> f, final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <F, G, H> @NotNull P8<FN, A, B, C, D, E, F, G, H> and(final @NotNull App<FN, F> f, final @NotNull App<FN, G> g,
                                                                     final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function5<A, B, C, D, E, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function5<A, B, C, D, E, R>> function) {
            return instance.ap5(function, a, b, c, d, e);
        }
    }

    record P6<FN extends K1, A, B, C, D, E, F>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                               @NotNull App<FN, E> e, @NotNull App<FN, F> f) {

        public <G> P7<FN, A, B, C, D, E, F, G> and(final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <G, H> P8<FN, A, B, C, D, E, F, G, H> and(final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function6<A, B, C, D, E, F, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function6<A, B, C, D, E, F, R>> function) {
            return instance.ap6(function, a, b, c, d, e, f);
        }
    }

    record P7<FN extends K1, A, B, C, D, E, F, G>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                  @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance, final @NotNull Function7<A, B, C, D, E, F, G, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function7<A, B, C, D, E, F, G, R>> function) {
            return instance.ap7(function, a, b, c, d, e, f, g);
        }
    }

    record P8<FN extends K1, A, B, C, D, E, F, G, H>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                     @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g, @NotNull App<FN, H> h) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function8<A, B, C, D, E, F, G, H, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function8<A, B, C, D, E, F, G, H, R>> function) {
            return instance.ap8(function, a, b, c, d, e, f, g, h);
        }
    }

    record P9<FN extends K1, A, B, C, D, E, F, G, H, I>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                        @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g, @NotNull App<FN, H> h,
                                                        @NotNull App<FN, I> i) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function9<A, B, C, D, E, F, G, H, I, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function9<A, B, C, D, E, F, G, H, I, R>> function) {
            return instance.ap9(function, a, b, c, d, e, f, g, h, i);
        }
    }

    record P10<FN extends K1, A, B, C, D, E, F, G, H, I, J>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                            @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                            @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                            @NotNull App<FN, J> j) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function10<A, B, C, D, E, F, G, H, I, J, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function10<A, B, C, D, E, F, G, H, I, J, R>> function) {
            return instance.ap10(function, a, b, c, d, e, f, g, h, i, j);
        }
    }

    record P11<FN extends K1, A, B, C, D, E, F, G, H, I, J, K>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                               @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                               @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                               @NotNull App<FN, J> j, @NotNull App<FN, K> k) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function11<A, B, C, D, E, F, G, H, I, J, K, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function11<A, B, C, D, E, F, G, H, I, J, K, R>> function) {
            return instance.ap11(function, a, b, c, d, e, f, g, h, i, j, k);
        }
    }

    record P12<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                  @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                  @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                  @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function12<A, B, C, D, E, F, G, H, I, J, K, L, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function12<A, B, C, D, E, F, G, H, I, J, K, L, R>> function) {
            return instance.ap12(function, a, b, c, d, e, f, g, h, i, j, k, l);
        }
    }

    record P13<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                     @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                     @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                     @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                     @NotNull App<FN, M> m) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, R>> function) {
            return instance.ap13(function, a, b, c, d, e, f, g, h, i, j, k, l, m);
        }
    }

    record P14<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                        @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                        @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                        @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                        @NotNull App<FN, M> m, @NotNull App<FN, N> n) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R>> function) {
            return instance.ap14(function, a, b, c, d, e, f, g, h, i, j, k, l, m, n);
        }
    }

    record P15<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                           @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                           @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                           @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                           @NotNull App<FN, M> m, @NotNull App<FN, N> n, @NotNull App<FN, O> o) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R>> function) {
            return instance.ap15(function, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
        }
    }

    record P16<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                              @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                              @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                              @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                              @NotNull App<FN, M> m, @NotNull App<FN, N> n, @NotNull App<FN, O> o,
                                                                              @NotNull App<FN, P> p) {

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull Function16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R> function) {
            return apply(instance, instance.point(function));
        }

        public <R> @NotNull App<FN, R> apply(final @NotNull Applicative<FN, ?> instance,
                                             final @NotNull App<FN, Function16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R>> function) {
            return instance.ap16(function, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
        }
    }
}
