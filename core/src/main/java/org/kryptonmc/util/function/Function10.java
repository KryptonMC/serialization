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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/datafixers/util/Function10.java
 */
package org.kryptonmc.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function10<A, B, C, D, E, F, G, H, I, J, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

    default @NotNull Function<A, Function9<B, C, D, E, F, G, H, I, J, R>> curry() {
        return a -> (b, c, d, e, f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull BiFunction<A, B, Function8<C, D, E, F, G, H, I, J, R>> curry2() {
        return (a, b) -> (c, d, e, f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function3<A, B, C, Function7<D, E, F, G, H, I, J, R>> curry3() {
        return (a, b, c) -> (d, e, f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function4<A, B, C, D, Function6<E, F, G, H, I, J, R>> curry4() {
        return (a, b, c, d) -> (e, f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function5<A, B, C, D, E, Function5<F, G, H, I, J, R>> curry5() {
        return (a, b, c, d, e) -> (f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function6<A, B, C, D, E, F, Function4<G, H, I, J, R>> curry6() {
        return (a, b, c, d, e, f) -> (g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function7<A, B, C, D, E, F, G, Function3<H, I, J, R>> curry7() {
        return (a, b, c, d, e, f, g) -> (h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function8<A, B, C, D, E, F, G, H, BiFunction<I, J, R>> curry8() {
        return (a, b, c, d, e, f, g, h) -> (i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }

    default @NotNull Function9<A, B, C, D, E, F, G, H, I, Function<J, R>> curry9() {
        return (a, b, c, d, e, f, g, h, i) -> j -> apply(a, b, c, d, e, f, g, h, i, j);
    }
}
