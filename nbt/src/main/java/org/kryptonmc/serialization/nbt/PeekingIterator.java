package org.kryptonmc.serialization.nbt;

import java.util.Iterator;

/**
 * An iterator that allows peeking one element in advanced.
 *
 * This is a port of Guava's peeking iterator, to avoid a dependency on Guava.
 */
interface PeekingIterator<E> extends Iterator<E> {

    E peek();
}
