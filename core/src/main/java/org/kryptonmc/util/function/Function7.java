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
public interface Function7<A, B, C, D, E, F, G, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g);

    default @NotNull Function<A, Function6<B, C, D, E, F, G, R>> curry() {
        return a -> (b, c, d, e, f, g) -> apply(a, b, c, d, e, f, g);
    }

    default @NotNull BiFunction<A, B, Function5<C, D, E, F, G, R>> curry2() {
        return (a, b) -> (c, d, e, f, g) -> apply(a, b, c, d, e, f, g);
    }

    default @NotNull Function3<A, B, C, Function4<D, E, F, G, R>> curry3() {
        return (a, b, c) -> (d, e, f, g) -> apply(a, b, c, d, e, f, g);
    }

    default @NotNull Function4<A, B, C, D, Function3<E, F, G, R>> curry4() {
        return (a, b, c, d) -> (e, f, g) -> apply(a, b, c, d, e, f, g);
    }

    default @NotNull Function5<A, B, C, D, E, BiFunction<F, G, R>> curry5() {
        return (a, b, c, d, e) -> (f, g) -> apply(a, b, c, d, e, f, g);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function<G, R>> curry6() {
        return (a, b, c, d, e, f) -> g -> apply(a, b, c, d, e, f, g);
    }
}
