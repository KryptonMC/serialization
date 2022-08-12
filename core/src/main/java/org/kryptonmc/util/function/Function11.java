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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function11.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function11<A, B, C, D, E, F, G, H, I, J, K, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k);

    default @NotNull Function<A, Function10<B, C, D, E, F, G, H, I, J, K, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull BiFunction<A, B, Function9<C, D, E, F, G, H, I, J, K, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function3<A, B, C, Function8<D, E, F, G, H, I, J, K, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function4<A, B, C, D, Function7<E, F, G, H, I, J, K, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function5<A, B, C, D, E, Function6<F, G, H, I, J, K, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function5<G, H, I, J, K, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function4<H, I, J, K, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, Function3<I, J, K, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, BiFunction<J, K, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> (j, k) -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }

    default @NotNull Function10<A, B, C, D, E, F, G, H, I, J, Function<K, R>> curry10() {
        return (a, b, c, d, e, f, g, h, i, j) -> k -> apply(a, b, c, d, e, f, g, h, i, j, k);
    }
}
