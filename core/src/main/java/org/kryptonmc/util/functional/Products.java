/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util.functional;

import org.jetbrains.annotations.NotNull;

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
    }

    record P6<FN extends K1, A, B, C, D, E, F>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                               @NotNull App<FN, E> e, @NotNull App<FN, F> f) {

        public <G> P7<FN, A, B, C, D, E, F, G> and(final @NotNull App<FN, G> g) {
            return new P7<>(a, b, c, d, e, f, g);
        }

        public <G, H> P8<FN, A, B, C, D, E, F, G, H> and(final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
            return new P8<>(a, b, c, d, e, f, g, h);
        }
    }

    record P7<FN extends K1, A, B, C, D, E, F, G>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                  @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g) {}

    record P8<FN extends K1, A, B, C, D, E, F, G, H>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                     @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g, @NotNull App<FN, H> h) {}

    record P9<FN extends K1, A, B, C, D, E, F, G, H, I>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c, @NotNull App<FN, D> d,
                                                        @NotNull App<FN, E> e, @NotNull App<FN, F> f, @NotNull App<FN, G> g, @NotNull App<FN, H> h,
                                                        @NotNull App<FN, I> i) {}

    record P10<FN extends K1, A, B, C, D, E, F, G, H, I, J>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                            @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                            @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                            @NotNull App<FN, J> j) {}

    record P11<FN extends K1, A, B, C, D, E, F, G, H, I, J, K>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                               @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                               @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                               @NotNull App<FN, J> j, @NotNull App<FN, K> k) {}

    record P12<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                  @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                  @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                  @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l) {}

    record P13<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                     @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                     @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                     @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                     @NotNull App<FN, M> m) {}

    record P14<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                        @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                        @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                        @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                        @NotNull App<FN, M> m, @NotNull App<FN, N> n) {}

    record P15<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                           @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                           @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                           @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                           @NotNull App<FN, M> m, @NotNull App<FN, N> n, @NotNull App<FN, O> o) {}

    record P16<FN extends K1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>(@NotNull App<FN, A> a, @NotNull App<FN, B> b, @NotNull App<FN, C> c,
                                                                              @NotNull App<FN, D> d, @NotNull App<FN, E> e, @NotNull App<FN, F> f,
                                                                              @NotNull App<FN, G> g, @NotNull App<FN, H> h, @NotNull App<FN, I> i,
                                                                              @NotNull App<FN, J> j, @NotNull App<FN, K> k, @NotNull App<FN, L> l,
                                                                              @NotNull App<FN, M> m, @NotNull App<FN, N> n, @NotNull App<FN, O> o,
                                                                              @NotNull App<FN, P> p) {}
}
