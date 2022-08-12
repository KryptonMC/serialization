/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 *
 * For the original files that this file is derived from, see here:
 * https://github.com/google/guava/blob/5c8719e28880a0f942272bdd57d9a194a2d6226c/guava/src/com/google/common/collect/TransformedIterator.java
 *
 * Changes made (required by Apache License 2.0):
 * * Removed original documentation
 * * Removed nullable object supertypes on the generic types.
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
