/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.functional.App;
import org.kryptonmc.util.functional.Applicative;
import org.kryptonmc.util.functional.CartesianLike;
import org.kryptonmc.util.functional.K1;
import org.kryptonmc.util.functional.Traversable;

public record Pair<F, S>(@Nullable F first, @Nullable S second) implements App<Pair.Mu<S>, F> {

    public static <F, S> @NotNull Pair<F, S> unbox(final @NotNull App<Mu<S>, F> box) {
        return (Pair<F, S>) box;
    }

    public static <F, S> @NotNull Pair<F, S> of(final @Nullable F first, final @Nullable S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> @NotNull Collector<Pair<F, S>, ?, Map<F, S>> toMap() {
        return Collectors.toMap(Pair::first, Pair::second);
    }

    public <F2> @NotNull Pair<F2, S> mapFirst(final @NotNull Function<? super F, ? extends F2> function) {
        return of(function.apply(first), second);
    }

    public <S2> @NotNull Pair<F, S2> mapSecond(final @NotNull Function<? super S, ? extends S2> function) {
        return of(first, function.apply(second));
    }

    public @NotNull Pair<S, F> swap() {
        return of(second, first);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public static final class Mu<S> implements K1 {}

    public static final class Instance<S2> implements Traversable<Mu<S2>, Instance.Mu<S2>>, CartesianLike<Mu<S2>, S2, Instance.Mu<S2>> {

        @Override
        public <T, R> @NotNull App<Pair.Mu<S2>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                       final @NotNull App<Pair.Mu<S2>, T> argument) {
            return Pair.unbox(argument).mapFirst(function);
        }

        @Override
        public <F extends K1, A, B> @NotNull App<F, App<Pair.Mu<S2>, B>> traverse(final @NotNull Applicative<F, ?> applicative,
                                                                                  final @NotNull Function<A, App<F, B>> function,
                                                                                  final @NotNull App<Pair.Mu<S2>, A> input) {
            final Pair<A, S2> pair = Pair.unbox(input);
            return applicative.ap(b -> of(b, pair.second), function.apply(pair.first));
        }

        @Override
        public <A> @NotNull App<Pair.Mu<S2>, A> to(final @NotNull App<Pair.Mu<S2>, A> input) {
            return input;
        }

        @Override
        public <A> @NotNull App<Pair.Mu<S2>, A> from(final @NotNull App<Pair.Mu<S2>, A> input) {
            return input;
        }

        public static final class Mu<S2> implements Traversable.Mu, CartesianLike.Mu {}
    }
}
