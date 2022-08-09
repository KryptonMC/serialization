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
public interface Function5<A, B, C, D, E, R> {

    R apply(A a, B b, C c, D d, E e);

    default @NotNull Function<A, Function4<B, C, D, E, R>> curry() {
        return a -> (b, c, d, e) -> apply(a, b, c, d, e);
    }

    default @NotNull BiFunction<A, B, Function3<C, D, E, R>> curry2() {
        return (a, b) -> (c, d, e) -> apply(a, b, c, d, e);
    }

    default @NotNull Function3<A, B, C, BiFunction<D, E, R>> curry3() {
        return (a, b, c) -> (d, e) -> apply(a, b, c, d, e);
    }

    default @NotNull Function4<A, B, C, D, Function<E, R>> curry4() {
        return (a, b, c, d) -> (e) -> apply(a, b, c, d, e);
    }
}
