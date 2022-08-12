/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
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

public interface Applicative<FN extends K1, Mu extends Applicative.Mu> extends Functor<FN, Mu> {

    <A> @NotNull App<FN, A> point(final @NotNull A a);

    <A, R> @NotNull Function<App<FN, A>, App<FN, R>> lift1(final @NotNull App<FN, Function<A, R>> function);

    default <A, B, R> @NotNull BiFunction<App<FN, A>, App<FN, B>, App<FN, R>> lift2(final @NotNull App<FN, BiFunction<A, B, R>> function) {
        return (fa, fb) -> ap2(function, fa, fb);
    }

    default <A, R> @NotNull App<FN, R> ap(final @NotNull App<FN, Function<A, R>> function, final @NotNull App<FN, A> argument) {
        return lift1(function).apply(argument);
    }

    default <A, R> @NotNull App<FN, R> ap(final @NotNull Function<A, R> function, final @NotNull App<FN, A> argument) {
        return map(function, argument);
    }

    default <A, B, R> @NotNull App<FN, R> ap2(final @NotNull App<FN, BiFunction<A, B, R>> function, final @NotNull App<FN, A> a,
                                              final @NotNull App<FN, B> b) {
        final Function<BiFunction<A, B, R>, Function<A, Function<B, R>>> curry = f -> a1 -> b1 -> f.apply(a1, b1);
        return ap(ap(map(curry, function), a), b);
    }

    default <A, B, C, R> @NotNull App<FN, R> ap3(final @NotNull App<FN, Function3<A, B, C, R>> function, final @NotNull App<FN, A> a,
                                                 final @NotNull App<FN, B> b, final @NotNull App<FN, C> c) {
        return ap2(ap(map(Function3::curry, function), a), b, c);
    }

    default <A, B, C, D, R> @NotNull App<FN, R> ap4(final @NotNull App<FN, Function4<A, B, C, D, R>> function, final @NotNull App<FN, A> a,
                                                    final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d) {
        return ap2(ap2(map(Function4::curry2, function), a, b), c, d);
    }

    default <A, B, C, D, E, R> @NotNull App<FN, R> ap5(final @NotNull App<FN, Function5<A, B, C, D, E, R>> func, final @NotNull App<FN, A> a,
                                                       final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
                                                       final @NotNull App<FN, E> e) {
        return ap3(ap2(map(Function5::curry2, func), a, b), c, d, e);
    }

    default <A, B, C, D, E, F, R> @NotNull App<FN, R> ap6(final @NotNull App<FN, Function6<A, B, C, D, E, F, R>> func, final @NotNull App<FN, A> a,
                                                          final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d,
                                                          final @NotNull App<FN, E> e, final @NotNull App<FN, F> f) {
        return ap3(ap3(map(Function6::curry3, func), a, b, c), d, e, f);
    }

    default <A, B, C, D, E, F, G, R> @NotNull App<FN, R> ap7(
            final @NotNull App<FN, Function7<A, B, C, D, E, F, G, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g) {
        return ap4(ap3(map(Function7::curry3, func), a, b, c), d, e, f, g);
    }

    default <A, B, C, D, E, F, G, H, R> @NotNull App<FN, R> ap8(
            final @NotNull App<FN, Function8<A, B, C, D, E, F, G, H, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g, final @NotNull App<FN, H> h) {
        return ap4(ap4(map(Function8::curry4, func), a, b, c, d), e, f, g, h);
    }

    default <A, B, C, D, E, F, G, H, I, R> @NotNull App<FN, R> ap9(
            final @NotNull App<FN, Function9<A, B, C, D, E, F, G, H, I, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i) {
        return ap5(ap4(map(Function9::curry4, func), a, b, c, d), e, f, g, h, i);
    }

    default <A, B, C, D, E, F, G, H, I, J, R> @NotNull App<FN, R> ap10(
            final @NotNull App<FN, Function10<A, B, C, D, E, F, G, H, I, J, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i, final @NotNull App<FN, J> j) {
        return ap5(ap5(map(Function10::curry5, func), a, b, c, d, e), f, g, h, i, j);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, R> @NotNull App<FN, R> ap11(
            final @NotNull App<FN, Function11<A, B, C, D, E, F, G, H, I, J, K, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i, final @NotNull App<FN, J> j,
            final @NotNull App<FN, K> k) {
        return ap6(ap5(map(Function11::curry5, func), a, b, c, d, e), f, g, h, i, j, k);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, R> @NotNull App<FN, R> ap12(
            final @NotNull App<FN, Function12<A, B, C, D, E, F, G, H, I, J, K, L, R>> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b,
            final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e, final @NotNull App<FN, F> f,
            final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i, final @NotNull App<FN, J> j,
            final @NotNull App<FN, K> k, final @NotNull App<FN, L> l) {
        return ap6(ap6(map(Function12::curry6, func), a, b, c, d, e, f), g, h, i, j, k, l);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, R> @NotNull App<FN, R> ap13(
            final @NotNull App<FN, Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, R>> func, final @NotNull App<FN, A> a,
            final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
            final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i,
            final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l, final @NotNull App<FN, M> m) {
        return ap7(ap6(map(Function13::curry6, func), a, b, c, d, e, f), g, h, i, j, k, l, m);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> @NotNull App<FN, R> ap14(
            final @NotNull App<FN, Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R>> func, final @NotNull App<FN, A> a,
            final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
            final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i,
            final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l, final @NotNull App<FN, M> m,
            final @NotNull App<FN, N> n) {
        return ap7(ap7(map(Function14::curry7, func), a, b, c, d, e, f, g), h, i, j, k, l, m, n);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> @NotNull App<FN, R> ap15(
            final @NotNull App<FN, Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R>> func, final @NotNull App<FN, A> a,
            final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
            final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i,
            final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l, final @NotNull App<FN, M> m,
            final @NotNull App<FN, N> n, final @NotNull App<FN, O> o) {
        return ap8(ap7(map(Function15::curry7, func), a, b, c, d, e, f, g), h, i, j, k, l, m, n, o);
    }

    default <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R> @NotNull App<FN, R> ap16(
            final @NotNull App<FN, Function16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R>> func, final @NotNull App<FN, A> a,
            final @NotNull App<FN, B> b, final @NotNull App<FN, C> c, final @NotNull App<FN, D> d, final @NotNull App<FN, E> e,
            final @NotNull App<FN, F> f, final @NotNull App<FN, G> g, final @NotNull App<FN, H> h, final @NotNull App<FN, I> i,
            final @NotNull App<FN, J> j, final @NotNull App<FN, K> k, final @NotNull App<FN, L> l, final @NotNull App<FN, M> m,
            final @NotNull App<FN, N> n, final @NotNull App<FN, O> o, final @NotNull App<FN, P> p) {
        return ap8(ap8(map(Function16::curry8, func), a, b, c, d, e, f, g, h), i, j, k, l, m, n, o, p);
    }

    default <A, B, R> @NotNull App<FN, R> apply2(final @NotNull BiFunction<A, B, R> func, final @NotNull App<FN, A> a, final @NotNull App<FN, B> b) {
        return ap2(point(func), a, b);
    }

    interface Mu extends Functor.Mu {}
}
