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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function12.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function12<A, B, C, D, E, F, G, H, I, J, K, L, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l);

    default @NotNull Function<A, Function11<B, C, D, E, F, G, H, I, J, K, L, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull BiFunction<A, B, Function10<C, D, E, F, G, H, I, J, K, L, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function3<A, B, C, Function9<D, E, F, G, H, I, J, K, L, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function4<A, B, C, D, Function8<E, F, G, H, I, J, K, L, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function5<A, B, C, D, E, Function7<F, G, H, I, J, K, L, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function6<G, H, I, J, K, L, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function5<H, I, J, K, L, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function4<I, J, K, L, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, Function3<J, K, L, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> (j, k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function10<A, B, C, D, E, F, G, H, I, J, BiFunction<K, L, R>> curry10() {
        return (a, b, c, d, e, f, g, h, i, j) -> (k, l) -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    default @NotNull Function11<A, B, C, D, E, F, G, H, I, J, K, Function<L, R>> curry11() {
        return (a, b, c, d, e, f, g, h, i, j, k) -> l -> apply(a, b, c, d, e, f, g, h, i, j, k, l);
    }
}
