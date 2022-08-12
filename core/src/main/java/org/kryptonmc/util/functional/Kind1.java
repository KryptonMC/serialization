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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/kinds/Kind1.java
 */
package org.kryptonmc.util.functional;

import org.jetbrains.annotations.NotNull;

/**
 * This may look like a mess and make no sense, but what it allows us to do is
 * incredible. When we're building new compound codecs, we can call these group
 * functions and allow us to determine the correct applicable function with the
 * right amount of parameters, all of the correct type, when we want to call
 * the constructor.
 */
public interface Kind1<FN extends K1, Mu extends Kind1.Mu> extends App<Mu, FN> {

    default <A> Products.@NotNull P1<FN, A> group(final @NotNull App<FN, A> a) {
        return new Products.P1<>(a);
    }

    default <A, B> Products.@NotNull P2<FN, A, B> group(final @NotNull App<FN, A> a, final @NotNull App<FN, B> b) {
        return new Products.P2<>(a, b);
    }

    default <A, B, C> Products.@NotNull P3<FN, A, B, C> group(final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
                                                              final @NotNull App<FN, C> c) {
        return new Products.P3<>(a, b, c);
    }

    default <A, B, C, D> Products.@NotNull P4<FN, A, B, C, D> group(final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
                                                                    final @NotNull App<FN, C> c, final @NotNull App<FN, D> d) {
        return new Products.P4<>(a, b, c, d);
    }

    default <A, B, C, D, E> Products.@NotNull P5<FN, A, B, C, D, E> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e) {
        return new Products.P5<>(a, b, c, d, e);
    }

    default <A, B, C, D, E, F> Products.@NotNull P6<FN, A, B, C, D, E, F> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f) {
        return new Products.P6<>(a, b, c, d, e, f);
    }

    default <A, B, C, D, E, F, G> Products.@NotNull P7<FN, A, B, C, D, E, F, G> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g) {
        return new Products.P7<>(a, b, c, d, e, f, g);
    }

    default <A, B, C, D, E, F, G, H> Products.@NotNull P8<FN, A, B, C, D, E, F, G, H> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
        return new Products.P8<>(a, b, c, d, e, f, g, h);
    }

    default <A, B, C, D, E, F, G, H, I> Products.@NotNull P9<FN, A, B, C, D, E, F, G, H, I> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i) {
        return new Products.P9<>(a, b, c, d, e, f, g, h, i);
    }

    default <A, B, C, D, E, F, G, H, I, J> Products.@NotNull P10<FN, A, B, C, D, E, F, G, H, I, J> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j) {
        return new Products.P10<>(a, b, c, d, e, f, g, h, i, j);
    }

    default <A, B, C, D, E, F, G, H, I, J, K> Products.@NotNull P11<FN, A, B, C, D, E, F, G, H, I, J, K> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k) {
        return new Products.P11<>(a, b, c, d, e, f, g, h, i, j, k);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L> Products.@NotNull P12<FN, A, B, C, D, E, F, G, H, I, J, K, L> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l) {
        return new Products.P12<>(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M> Products.@NotNull P13<FN, A, B, C, D, E, F, G, H, I, J, K, L, M> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l,
            final @NotNull App<FN, M> m) {
        return new Products.P13<>(a, b, c, d, e, f, g, h, i, j, k, l, m);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Products.@NotNull P14<FN, A, B, C, D, E, F, G, H, I, J, K, L, M, N> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l,
            final @NotNull App<FN, M> m, final @NotNull App<FN, N> n) {
        return new Products.P14<>(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Products.@NotNull P15<FN, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l,
            final @NotNull App<FN, M> m, final @NotNull App<FN, N> n, final @NotNull App<FN, O> o) {
        return new Products.P15<>(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Products.@NotNull P16<FN, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> group(
            final @NotNull App<FN, A> a, final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
            final @NotNull App<FN, E> e, final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h,
            final @NotNull App<FN, I> i, final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l,
            final @NotNull App<FN, M> m, final @NotNull App<FN, N> n, final @NotNull App<FN, O> o, final @NotNull App<FN, P> p) {
        return new Products.P16<>(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    interface Mu extends K1 {}
}
