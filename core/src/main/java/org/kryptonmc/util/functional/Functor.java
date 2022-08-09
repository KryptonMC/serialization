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

public interface Functor<F extends K1, Mu extends Functor.Mu> extends Kind1<F, Mu> {

    <T, R> @NotNull App<F, R> map(@NotNull Function<? super T, ? extends R> function, @NotNull App<F, T> argument);

    interface Mu extends Kind1.Mu {}
}
