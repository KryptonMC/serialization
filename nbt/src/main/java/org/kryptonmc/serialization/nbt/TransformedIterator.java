package org.kryptonmc.serialization.nbt;

import java.util.Iterator;

/**
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
