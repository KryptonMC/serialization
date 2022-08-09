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
public interface Function6<A, B, C, D, E, F, R> {

    R apply(A a, B b, C c, D d, E e, F f);

    default @NotNull Function<A, Function5<B, C, D, E, F, R>> curry() {
        return a -> (b, c, d, e, f) -> apply(a, b, c, d, e, f);
    }

    default @NotNull BiFunction<A, B, Function4<C, D, E, F, R>> curry2() {
        return (a, b) -> (c, d, e, f) -> apply(a, b, c, d, e, f);
    }

    default @NotNull Function3<A, B, C, Function3<D, E, F, R>> curry3() {
        return (a, b, c) -> (d, e, f) -> apply(a, b, c, d, e, f);
    }

    default @NotNull Function4<A, B, C, D, BiFunction<E, F, R>> curry4() {
        return (a, b, c, d) -> (e, f) -> apply(a, b, c, d, e, f);
    }

    default @NotNull Function5<A, B, C, D, E, Function<F, R>> curry5() {
        return (a, b, c, d, e) -> f -> apply(a, b, c, d, e, f);
    }
}
