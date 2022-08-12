/*
 * This file is part of Krypton Serialization, and originates from Google
 * Guava, originally licensed under the Apache License 2.0, re-licensed here
 * under the MIT license.
 *
 * Copyright (C) 2008 The Guava Authors
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 *
 * For the original files that this file is derived from, see here:
 * https://github.com/google/guava/blob/5c8719e28880a0f942272bdd57d9a194a2d6226c/guava/src/com/google/common/collect/PeekingIterator.java
 *
 * Changes made (required by Apache License 2.0):
 * * Removed original documentation
 * * Removed override of next() and remove()
 */
package org.kryptonmc.serialization.nbt;

import java.util.Iterator;

/*
 * An iterator that allows peeking one element in advanced.
 *
 * This is a port of Guava's peeking iterator, to avoid a dependency on Guava.
 */
interface PeekingIterator<E> extends Iterator<E> {

    E peek();
}
