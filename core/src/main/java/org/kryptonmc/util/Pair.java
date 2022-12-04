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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Pair.java
 */
package org.kryptonmc.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.functional.App;
import org.kryptonmc.util.functional.Applicative;
import org.kryptonmc.util.functional.CartesianLike;
import org.kryptonmc.util.functional.K1;
import org.kryptonmc.util.functional.Traversable;

/**
 * A pair of values.
 *
 * @param first The first value.
 * @param second The second value.
 * @param <F> The type of the first value.
 * @param <S> The type of the second value.
 */
public record Pair<F, S>(F first, S second) implements App<Pair.Mu<S>, F> {

    /**
     * Creates a new pair of values.
     *
     * @param first The first value.
     * @param second The second value.
     * @param <F> The first type.
     * @param <S> The second type.
     * @return The pair.
     */
    public static <F, S> @NotNull Pair<F, S> of(final F first, final S second) {
        return new Pair<>(first, second);
    }

    /**
     * Returns a collector that can convert a {@link java.util.stream.Stream}
     * of pairs in to a {@link java.util.Map}.
     *
     * @param <F> The first type.
     * @param <S> The second type.f
     * @return The collector.
     */
    public static <F, S> @NotNull Collector<Pair<F, S>, ?, Map<F, S>> toMap() {
        return Collectors.toMap(Pair::first, Pair::second);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Pair {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
    }

    /**
     * Maps the first value of the pair to a new pair with the first type as
     * the given {@link F2} type.
     *
     * @param mapper The function to map the first value with.
     * @param <F2> The mapped first type.
     * @return The mapped pair.
     */
    public <F2> @NotNull Pair<F2, S> mapFirst(final @NotNull Function<? super F, ? extends F2> mapper) {
        return of(mapper.apply(first), second);
    }

    /**
     * Maps the second value of the pair to a new pair with the second type as
     * the given {@link S2} type.
     *
     * @param mapper The function to map the second value with.
     * @param <S2> The mapped second type.
     * @return The mapped pair.
     */
    public <S2> @NotNull Pair<F, S2> mapSecond(final @NotNull Function<? super S, ? extends S2> mapper) {
        return of(first, mapper.apply(second));
    }

    /**
     * Maps both values of the pair to a new pair with the first type as the
     * given {@link F2} type and the second type as the given {@link S2} type.
     *
     * @param firstMapper The function to map the first value with.
     * @param secondMapper The function to map the second value with.
     * @param <F2> The mapped first type.
     * @param <S2> The mapped second type.
     * @return The mapped pair.
     */
    public <F2, S2> @NotNull Pair<F2, S2> mapBoth(final @NotNull Function<? super F, ? extends F2> firstMapper,
                                                      final @NotNull Function<? super S, ? extends S2> secondMapper) {
        return of(firstMapper.apply(first), secondMapper.apply(second));
    }

    /**
     * Swaps the values in this pair, meaning the second value becomes the
     * first, and the first value becomes the second.
     *
     * @return The swapped pair.
     */
    public @NotNull Pair<S, F> swap() {
        return of(second, first);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * A mu for the {@link Pair} applicative.
     *
     * @param <S> The second type of the {@link Pair}.
     */
    public static final class Mu<S> implements K1 {

        private Mu() {
        }
    }

    /**
     * An instance of the {@link Pair} applicative.
     *
     * @param <S2> The second type of the {@link Pair}.
     */
    public static final class Instance<S2> implements Traversable<Mu<S2>, Instance.Mu<S2>>, CartesianLike<Mu<S2>, S2, Instance.Mu<S2>> {

        /**
         * Creates a new instance of the {@link Pair} applicative.
         */
        public Instance() {
        }

        @Override
        public <T, R> @NotNull App<Pair.Mu<S2>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                       final @NotNull App<Pair.Mu<S2>, T> argument) {
            return unbox(argument).mapFirst(function);
        }

        @Override
        public <F extends K1, A, B> @NotNull App<F, App<Pair.Mu<S2>, B>> traverse(final @NotNull Applicative<F, ?> applicative,
                                                                                  final @NotNull Function<A, App<F, B>> function,
                                                                                  final @NotNull App<Pair.Mu<S2>, A> input) {
            final Pair<A, S2> pair = unbox(input);
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

        private static <F, S> @NotNull Pair<F, S> unbox(final @NotNull App<Pair.Mu<S>, F> box) {
            return (Pair<F, S>) box;
        }

        /**
         * A mu for the instance of the {@link Pair} applicative.
         *
         * @param <S2> The second type of the {@link Pair}.
         */
        public static final class Mu<S2> implements Traversable.Mu, CartesianLike.Mu {

            private Mu() {
            }
        }
    }
}
