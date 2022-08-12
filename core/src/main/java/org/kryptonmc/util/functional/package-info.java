/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
/**
 * This package contains various types modelled after functional programming
 * concepts that are mainly used in {@link org.kryptonmc.serialization.codecs.RecordCodecBuilder}
 * to allow clean complex serializers with a specific API that avoids the need
 * to write a boilerplate encoder by only specifying the codecs that are used
 * for encoding and decoding and how we create the custom object that the
 * record codec builder will create.
 *
 * <p>These may have a use outside of this library, though these are not
 * intended to be used outside of this library, and use of these types is not
 * recommended, as they are subject to change.</p>
 */
package org.kryptonmc.util.functional;
