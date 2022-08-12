/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.nbt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Some utilities ported from Guava to avoid a dependency on Guava.
final class Iterators {

    // Ported from Guava Lists.newArrayList(Iterator)
    public static <E> @NotNull List<E> asList(final @NotNull Iterator<? extends E> elements) {
        final ArrayList<E> list = new ArrayList<>();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }

    public static <F, T> @NotNull Iterator<T> transform(final @NotNull Iterator<F> from, final @NotNull Function<? super F, ? extends T> function) {
        return new TransformedIterator<>(from) {
            @Override
            T transform(final F from) {
                return function.apply(from);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull PeekingIterator<T> peekingIterator(final @NotNull Iterator<? extends T> iterator) {
        if (iterator instanceof PeekingImpl<?>) return (PeekingImpl<T>) iterator;
        return new PeekingImpl<>(iterator);
    }

    private Iterators() {
    }

    private static final class PeekingImpl<E> implements PeekingIterator<E> {

        private final Iterator<? extends E> iterator;
        private boolean hasPeeked;
        private @Nullable E peekedElement;

        PeekingImpl(final Iterator<? extends E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return hasPeeked || iterator.hasNext();
        }

        @Override
        public E next() {
            if (!hasPeeked) iterator.next();
            final E result = peekedElement;
            hasPeeked = false;
            peekedElement = null;
            return result;
        }

        @Override
        public void remove() {
            if (hasPeeked) throw new IllegalStateException("Can't remove after you've peeked at next");
            iterator.remove();
        }

        @Override
        public E peek() {
            if (!hasPeeked) {
                peekedElement = iterator.next();
                hasPeeked = true;
            }
            return peekedElement;
        }
    }
}
