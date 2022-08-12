/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.util;

/**
 * A constant value. This is like {@link Void}, but it is a singleton that can
 * be returned.
 */
public enum Unit {

    INSTANCE;

    @Override
    public String toString() {
        return "Unit";
    }
}
