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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function15.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o);

    default @NotNull Function<A, Function14<B, C, D, E, F, G, H, I, J, K, L, M, N, O, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull BiFunction<A, B, Function13<C, D, E, F, G, H, I, J, K, L, M, N, O, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function3<A, B, C, Function12<D, E, F, G, H, I, J, K, L, M, N, O, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function4<A, B, C, D, Function11<E, F, G, H, I, J, K, L, M, N, O, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function5<A, B, C, D, E, Function10<F, G, H, I, J, K, L, M, N, O, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function9<G, H, I, J, K, L, M, N, O, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function8<H, I, J, K, L, M, N, O, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function7<I, J, K, L, M, N, O, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, Function6<J, K, L, M, N, O, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> (j, k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function10<A, B, C, D, E, F, G, H, I, J, Function5<K, L, M, N, O, R>> curry10() {
        return (a, b, c, d, e, f, g, h, i, j) -> (k, l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function11<A, B, C, D, E, F, G, H, I, J, K, Function4<L, M, N, O, R>> curry11() {
        return (a, b, c, d, e, f, g, h, i, j, k) -> (l, m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function12<A, B, C, D, E, F, G, H, I, J, K, L, Function3<M, N, O, R>> curry12() {
        return (a, b, c, d, e, f, g, h, i, j, k, l) -> (m, n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, BiFunction<N, O, R>> curry13() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m) -> (n, o) -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }

    default @NotNull Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, Function<O, R>> curry14() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> o -> apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
    }
}
