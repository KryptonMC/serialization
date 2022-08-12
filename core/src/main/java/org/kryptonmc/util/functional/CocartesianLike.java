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
import org.kryptonmc.util.Either;

public interface CocartesianLike<T extends K1, C, Mu extends CocartesianLike.Mu> extends Functor<T, Mu>, Traversable<T, Mu> {

    <A> @NotNull App<Either.Mu<C>, A> to(final @NotNull App<T, A> input);

    <A> @NotNull App<T, A> from(final @NotNull App<Either.Mu<C>, A> input);

    @Override
    default @NotNull <F extends K1, A, B> App<F, App<T, B>> traverse(
            final @NotNull Applicative<F, ?> applicative, final @NotNull Function<A, App<F, B>> function, final @NotNull App<T, A> input) {
        return applicative.map(this::from, new Either.Instance<C>().traverse(applicative, function, to(input)));
    }

    interface Mu extends Functor.Mu, Traversable.Mu {}
}
