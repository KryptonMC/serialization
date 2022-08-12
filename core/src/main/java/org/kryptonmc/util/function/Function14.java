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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function14.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n);

    default @NotNull Function<A, Function13<B, C, D, E, F, G, H, I, J, K, L, M, N, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull BiFunction<A, B, Function12<C, D, E, F, G, H, I, J, K, L, M, N, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function3<A, B, C, Function11<D, E, F, G, H, I, J, K, L, M, N, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function4<A, B, C, D, Function10<E, F, G, H, I, J, K, L, M, N, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function5<A, B, C, D, E, Function9<F, G, H, I, J, K, L, M, N, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function8<G, H, I, J, K, L, M, N, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function7<H, I, J, K, L, M, N, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function6<I, J, K, L, M, N, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, Function5<J, K, L, M, N, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> (j, k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function10<A, B, C, D, E, F, G, H, I, J, Function4<K, L, M, N, R>> curry10() {
        return (a, b, c, d, e, f, g, h, i, j) -> (k, l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function11<A, B, C, D, E, F, G, H, I, J, K, Function3<L, M, N, R>> curry11() {
        return (a, b, c, d, e, f, g, h, i, j, k) -> (l, m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function12<A, B, C, D, E, F, G, H, I, J, K, L, BiFunction<M, N, R>> curry12() {
        return (a, b, c, d, e, f, g, h, i, j, k, l) -> (m, n) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default @NotNull Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, Function<N, R>> curry13() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m) -> n -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }
}
