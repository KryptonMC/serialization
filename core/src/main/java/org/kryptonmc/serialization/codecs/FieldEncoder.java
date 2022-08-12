/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.Encoder;
import org.kryptonmc.serialization.MapEncoder;
import org.kryptonmc.serialization.RecordBuilder;

/**
 * A map encoder that will encode the input value with the given element
 * encoder and add it to the provided prefix with the given name as the key.
 *
 * @param name The field name.
 * @param elementEncoder The field value encoder.
 * @param <A> The input type.
 */
public record FieldEncoder<A>(@NotNull String name, @NotNull Encoder<A> elementEncoder) implements MapEncoder<A> {

    @Override
    public <T> @NotNull RecordBuilder<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
        return prefix.add(name, elementEncoder.encodeStart(input, ops));
    }

    @Override
    public String toString() {
        return "FieldEncoder[" + name + ": " + elementEncoder + ']';
    }
}
