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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/kinds/Functor.java
 */
package org.kryptonmc.util.functional;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface Functor<F extends K1, Mu extends Functor.Mu> extends Kind1<F, Mu> {

    <T, R> @NotNull App<F, R> map(final @NotNull Function<? super T, ? extends R> function, final @NotNull App<F, T> argument);

    interface Mu extends Kind1.Mu {}
}
