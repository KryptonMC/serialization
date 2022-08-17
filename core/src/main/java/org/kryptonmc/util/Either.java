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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Either.java
 */
package org.kryptonmc.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.functional.App;
import org.kryptonmc.util.functional.Applicative;
import org.kryptonmc.util.functional.CocartesianLike;
import org.kryptonmc.util.functional.K1;
import org.kryptonmc.util.functional.Traversable;

/**
 * A wrapper type that represents either a {@link L left} value or a
 * {@link R right} value. Only one of these values can be present at any given
 * time.
 *
 * @param <L> The left type.
 * @param <R> The right type.
 */
public sealed interface Either<L, R> extends App<Either.Mu<R>, L> permits Left, Right {

    /**
     * Creates a new either value that wraps the given left value.
     *
     * @param value The wrapped left value.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A new either that wraps the value.
     */
    static <L, R> @NotNull Either<L, R> left(final @NotNull L value) {
        return new Left<>(value);
    }

    /**
     * Creates a new either value that wraps the given right value.
     *
     * @param value The wrapped right value.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A new either that wraps the value.
     */
    static <L, R> @NotNull Either<L, R> right(final @NotNull R value) {
        return new Right<>(value);
    }

    /**
     * Gets the left value of this either, if present. This will only be
     * present if this either is a left either, else it will always return
     * {@link Optional#empty()}.
     *
     * @return The left value, or empty if not present.
     */
    @NotNull Optional<L> left();

    /**
     * Gets the right value of this either, if present. This will only be
     * present if this either is a right either, else it will always return
     * {@link Optional#empty()}.
     *
     * @return The right value, or empty if not present.
     */
    @NotNull Optional<R> right();

    /**
     * Runs the consumer if this either is left, else does nothing if this
     * either is right.
     *
     * @param consumer The consumer to run.
     * @return This either.
     */
    @Contract(value = "_ -> this", pure = true)
    @NotNull Either<L, R> ifLeft(final @NotNull Consumer<? super L> consumer);

    /**
     * Runs the consumer if this either is right, else does nothing if this
     * either is left.
     *
     * @param consumer The consumer to run.
     * @return This either.
     */
    @Contract(value = "_ -> this", pure = true)
    @NotNull Either<L, R> ifRight(final @NotNull Consumer<? super R> consumer);

    /**
     * Maps this either to a result value, running the left mapper if this
     * either is left, or running the right mapper if this either is right.
     *
     * @param leftMapper The mapper to run if this either is left.
     * @param rightMapper The mapper to run if this either is right.
     * @param <T> The result value from the mapping.
     * @return The mapped value.
     */
    <T> @NotNull T map(final @NotNull Function<? super L, ? extends T> leftMapper, final @NotNull Function<? super R, ? extends T> rightMapper);

    /**
     * Maps this either to a new either with the left value of {@link T}. If
     * this either is left, it will apply the mapper, else it will do nothing.
     *
     * @param mapper The mapper to run to map the left value.
     * @param <T> The resulting mapped left type.
     * @return The resulting mapped either.
     */
    default <T> @NotNull Either<T, R> mapLeft(final @NotNull Function<? super L, ? extends T> mapper) {
        return map(value -> left(mapper.apply(value)), Either::right);
    }

    /**
     * Maps this either to a new either with the right value of {@link T}. If
     * this either is right, it will apply the mapper, else it will do nothing.
     *
     * @param mapper The mapper to run to map the right value.
     * @param <T> The resulting mapped right type.
     * @return The resulting mapped either.
     */
    default <T> @NotNull Either<L, T> mapRight(final @NotNull Function<? super R, ? extends T> mapper) {
        return map(Either::left, value -> right(mapper.apply(value)));
    }

    /**
     * Maps this either to a new either with the left value of {@link A} and
     * the right value of {@link R}. If this either is left, it will apply the
     * left mapper to the value. If this either is right, it will apply the
     * right mapper to the value.
     *
     * @param leftMapper The mapper to apply if this either is left.
     * @param rightMapper The mapper to apply if this either is right.
     * @param <A> The resulting mapped left type.
     * @param <B> The resulting mapped right type.
     * @return The mapped either.
     */
    <A, B> @NotNull Either<A, B> mapBoth(final @NotNull Function<? super L, ? extends A> leftMapper,
                                         final @NotNull Function<? super R, ? extends B> rightMapper);

    /**
     * Maps this either to a new either with the left type {@link L2} by
     * applying the given function if this either is left, or creating a new
     * right either with the right value if this either is right.
     *
     * @param function The function to apply if this either is left.
     * @param <L2> The resulting mapped left type.
     * @return The mapped either.
     */
    default <L2> @NotNull Either<L2, R> flatMap(final @NotNull Function<L, Either<L2, R>> function) {
        return map(function, Either::right);
    }

    /**
     * Gets the value of this either if it is left, else it throws a
     * {@link RuntimeException} with the exception wrapped if the right value
     * is an exception, or the value if the right value is not an exception.
     *
     * @return The left value.
     * @throws RuntimeException If the value of this either is right.
     */
    default @NotNull L orThrow() {
        return map(left -> left, right -> {
            if (right instanceof final Throwable exception) throw new RuntimeException(exception);
            throw new RuntimeException(right.toString());
        });
    }

    /**
     * Swaps this either, returning a new right either with the left value if
     * this either is left, or a new left either with the right value if this
     * either is right.
     *
     * @return The swapped either.
     */
    default @NotNull Either<R, L> swap() {
        return map(Either::right, Either::left);
    }

    /**
     * A mu for the either applicative.
     *
     * @param <R> The right type of the Either.
     */
    class Mu<R> implements K1 {

        private Mu() {
        }
    }

    /**
     * An instance of the Either applicative.
     *
     * @param <R2> The type of the right value of the Either.
     */
    class Instance<R2> implements Applicative<Mu<R2>, Instance.Mu<R2>>, Traversable<Mu<R2>, Instance.Mu<R2>>,
            CocartesianLike<Mu<R2>, R2, Instance.Mu<R2>> {

        /**
         * Creates a new instance of the Either applicative.
         */
        public Instance() {
        }

        @Override
        public <T, R> @NotNull App<Either.Mu<R2>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                         final @NotNull App<Either.Mu<R2>, T> argument) {
            return unbox(argument).mapLeft(function);
        }

        @Override
        public <A> @NotNull App<Either.Mu<R2>, A> point(final @NotNull A a) {
            return left(a);
        }

        @Override
        public <A, R> @NotNull Function<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, R>> lift1(
                final @NotNull App<Either.Mu<R2>, Function<A, R>> function) {
            return a -> unbox(function).flatMap(f -> unbox(a).mapLeft(f));
        }

        @Override
        public <A, B, R> @NotNull BiFunction<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, B>, App<Either.Mu<R2>, R>> lift2(
                final @NotNull App<Either.Mu<R2>, BiFunction<A, B, R>> function) {
            return (a, b) -> unbox(function).flatMap(f -> unbox(a).flatMap(av -> unbox(b).mapLeft(bv -> f.apply(av, bv))));
        }

        @Override
        public <F extends K1, A, B> @NotNull App<F, App<Either.Mu<R2>, B>> traverse(final @NotNull Applicative<F, ?> applicative,
                                                                                    final @NotNull Function<A, App<F, B>> function,
                                                                                    final @NotNull App<Either.Mu<R2>, A> input) {
            return unbox(input).map(left -> applicative.ap(Either::left, function.apply(left)), right -> applicative.point(right(right)));
        }

        @Override
        public <A> @NotNull App<Either.Mu<R2>, A> to(final @NotNull App<Either.Mu<R2>, A> input) {
            return input;
        }

        @Override
        public <A> @NotNull App<Either.Mu<R2>, A> from(final @NotNull App<Either.Mu<R2>, A> input) {
            return input;
        }

        private static <L, R> @NotNull Either<L, R> unbox(final @NotNull App<Either.Mu<R>, L> box) {
            return (Either<L, R>) box;
        }

        /**
         * A mu for the instance applicative.
         *
         * @param <R2> The type of the right value of the Either.
         */
        public static final class Mu<R2> implements Applicative.Mu, Traversable.Mu, CocartesianLike.Mu {

            private Mu() {
            }
        }
    }
}
