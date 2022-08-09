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
public interface Function4<A, B, C, D, R> {

    R apply(A a, B b, C c, D d);

    default @NotNull Function<A, Function3<B, C, D, R>> curry() {
        return a -> (b, c, d) -> apply(a, b, c, d);
    }

    default @NotNull BiFunction<A, B, BiFunction<C, D, R>> curry2() {
        return (a, b) -> (c, d) -> apply(a, b, c, d);
    }

    default @NotNull Function3<A, B, C, Function<D, R>> curry3() {
        return (a, b, c) -> d -> apply(a, b, c, d);
    }
}
