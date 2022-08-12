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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function16.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p);

    default @NotNull Function<A, Function15<B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull BiFunction<A, B, Function14<C, D, E, F, G, H, I, J, K, L, M, N, O, P, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function3<A, B, C, Function13<D, E, F, G, H, I, J, K, L, M, N, O, P, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function4<A, B, C, D, Function12<E, F, G, H, I, J, K, L, M, N, O, P, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function5<A, B, C, D, E, Function11<F, G, H, I, J, K, L, M, N, O, P, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function10<G, H, I, J, K, L, M, N, O, P, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function9<H, I, J, K, L, M, N, O, P, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function8<I, J, K, L, M, N, O, P, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, Function7<J, K, L, M, N, O, P, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> (j, k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function10<A, B, C, D, E, F, G, H, I, J, Function6<K, L, M, N, O, P, R>> curry10() {
        return (a, b, c, d, e, f, g, h, i, j) -> (k, l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function11<A, B, C, D, E, F, G, H, I, J, K, Function5<L, M, N, O, P, R>> curry11() {
        return (a, b, c, d, e, f, g, h, i, j, k) -> (l, m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function12<A, B, C, D, E, F, G, H, I, J, K, L, Function4<M, N, O, P, R>> curry12() {
        return (a, b, c, d, e, f, g, h, i, j, k, l) -> (m, n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, Function3<N, O, P, R>> curry13() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m) -> (n, o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, BiFunction<O, P, R>> curry14() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> (o, p) ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default @NotNull Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Function<P, R>> curry15() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> p ->
                apply(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }
}
