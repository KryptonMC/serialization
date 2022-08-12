/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.nbt;

import java.util.Iterator;

/*
 * An iterator that transforms a backing iterator.
 *
 * This is a port of Guava's transformed iterator, to avoid a dependency on Guava.
 */
abstract class TransformedIterator<F, T> implements Iterator<T> {

    final Iterator<? extends F> backingIterator;

    TransformedIterator(final Iterator<? extends F> backingIterator) {
        this.backingIterator = backingIterator;
    }

    abstract T transform(final F from);

    @Override
    public final boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public final T next() {
        return transform(backingIterator.next());
    }

    @Override
    public final void remove() {
        backingIterator.remove();
    }
}
