/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util.functional;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.util.Pair;

public interface CartesianLike<T extends K1, C, Mu extends CartesianLike.Mu> extends Functor<T, Mu>, Traversable<T, Mu> {

    static <F extends K1, C, Mu extends CartesianLike.Mu> @NotNull CartesianLike<F, C, Mu> unbox(final @NotNull App<Mu, F> proofBox) {
        // noinspection unchecked
        return (CartesianLike<F, C, Mu>) proofBox;
    }

    <A> @NotNull App<Pair.Mu<C>, A> to(@NotNull App<T, A> input);

    <A> @NotNull App<T, A> from(@NotNull App<Pair.Mu<C>, A> input);

    @Override
    default <F extends K1, A, B> @NotNull App<F, App<T, B>> traverse(
            final @NotNull Applicative<F, ?> applicative, final @NotNull Function<A, App<F, B>> function, final @NotNull App<T, A> input) {
        return applicative.map(this::from, (new Pair.Instance<C>()).traverse(applicative, function, to(input)));
    }

    interface Mu extends Functor.Mu, Traversable.Mu {}
}
