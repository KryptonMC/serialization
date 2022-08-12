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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/kinds/Traversable.java
 */
package org.kryptonmc.util.functional;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface Traversable<T extends K1, Mu extends Traversable.Mu> extends Functor<T, Mu> {

    <F extends K1, A, B> @NotNull App<F, App<T, B>> traverse(
            final @NotNull Applicative<F, ?> applicative, final @NotNull Function<A, App<F, B>> function, final @NotNull App<T, A> input);

    default <F extends K1, A> @NotNull App<F, App<T, A>> flip(final @NotNull Applicative<F, ?> applicative, final @NotNull App<T, App<F, A>> input) {
        return traverse(applicative, Function.identity(), input);
    }

    interface Mu extends Functor.Mu {}
}
