/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function3<A, B, C, R> {

    R apply(A a, B b, C c);

    default @NotNull Function<A, BiFunction<B, C, R>> curry() {
        return a -> (b, c) -> apply(a, b, c);
    }

    default @NotNull BiFunction<A, B, Function<C, R>> curry2() {
        return (a, b) -> c -> apply(a, b, c);
    }
}
