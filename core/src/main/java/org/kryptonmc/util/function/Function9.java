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
public interface Function9<A, B, C, D, E, F, G, H, I, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);

    default @NotNull Function<A, Function8<B, C, D, E, F, G, H, I, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull BiFunction<A, B, Function7<C, D, E, F, G, H, I, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function3<A, B, C, Function6<D, E, F, G, H, I, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function4<A, B, C, D, Function5<E, F, G, H, I, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function5<A, B, C, D, E, Function4<F, G, H, I, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function3<G, H, I, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, BiFunction<H, I, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function<I, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> i -> apply(a, b, c, d, e, f, g, h, i);
    }
}
