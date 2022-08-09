/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
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

public sealed interface Either<L, R> extends App<Either.Mu<R>, L> permits Left, Right {

    static <L, R> @NotNull Either<L, R> unbox(final @NotNull App<Mu<R>, L> box) {
        return (Either<L, R>) box;
    }

    static <L, R> @NotNull Either<L, R> left(final @NotNull L value) {
        return new Left<>(value);
    }

    static <L, R> @NotNull Either<L, R> right(final @NotNull R value) {
        return new Right<>(value);
    }

    @NotNull Optional<L> left();

    @NotNull Optional<R> right();

    @Contract(value = "_ -> this", pure = true)
    @NotNull Either<L, R> ifLeft(@NotNull Consumer<? super L> consumer);

    @Contract(value = "_ -> this", pure = true)
    @NotNull Either<L, R> ifRight(@NotNull Consumer<? super R> consumer);

    <T> @NotNull T map(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper);

    default <T> @NotNull Either<T, R> mapLeft(final @NotNull Function<? super L, ? extends T> mapper) {
        return map(value -> left(mapper.apply(value)), Either::right);
    }

    default <T> @NotNull Either<L, T> mapRight(final @NotNull Function<? super R, ? extends T> mapper) {
        return map(Either::left, value -> right(mapper.apply(value)));
    }

    <A, B> @NotNull Either<A, B> mapBoth(@NotNull Function<? super L, ? extends A> leftMapper, @NotNull Function<? super R, ? extends B> rightMapper);

    default <L2> @NotNull Either<L2, R> flatMap(final @NotNull Function<L, Either<L2, R>> function) {
        return map(function, Either::right);
    }

    default @NotNull L orThrow() {
        return map(left -> left, right -> {
            if (right instanceof final Throwable exception) throw new RuntimeException(exception);
            throw new RuntimeException(right.toString());
        });
    }

    default @NotNull Either<R, L> swap() {
        return map(Either::right, Either::left);
    }

    class Mu<R> implements K1 {}

    class Instance<R2> implements Applicative<Mu<R2>, Instance.Mu<R2>>, Traversable<Mu<R2>, Instance.Mu<R2>>,
            CocartesianLike<Mu<R2>, R2, Instance.Mu<R2>> {

        @Override
        public @NotNull <T, R> App<Either.Mu<R2>, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                         final @NotNull App<Either.Mu<R2>, T> argument) {
            return unbox(argument).mapLeft(function);
        }

        @Override
        public @NotNull <A> App<Either.Mu<R2>, A> point(final @NotNull A a) {
            return left(a);
        }

        @Override
        public @NotNull <A, R> Function<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, R>> lift1(
                final @NotNull App<Either.Mu<R2>, Function<A, R>> function) {
            return a -> unbox(function).flatMap(f -> unbox(a).mapLeft(f));
        }

        @Override
        public @NotNull <A, B, R> BiFunction<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, B>, App<Either.Mu<R2>, R>> lift2(
                final @NotNull App<Either.Mu<R2>, BiFunction<A, B, R>> function) {
            return (a, b) -> unbox(function).flatMap(f -> unbox(a).flatMap(av -> unbox(b).mapLeft(bv -> f.apply(av, bv))));
        }

        @Override
        public @NotNull <F extends K1, A, B> App<F, App<Either.Mu<R2>, B>> traverse(final @NotNull Applicative<F, ?> applicative,
                                                                                    final @NotNull Function<A, App<F, B>> function,
                                                                                    final @NotNull App<Either.Mu<R2>, A> input) {
            return unbox(input).map(left -> applicative.ap(Either::left, function.apply(left)), right -> applicative.point(right(right)));
        }

        @Override
        public @NotNull <A> App<Either.Mu<R2>, A> to(final @NotNull App<Either.Mu<R2>, A> input) {
            return input;
        }

        @Override
        public @NotNull <A> App<Either.Mu<R2>, A> from(final @NotNull App<Either.Mu<R2>, A> input) {
            return input;
        }

        public static final class Mu<R2> implements Applicative.Mu, Traversable.Mu, CocartesianLike.Mu {}
    }
}
