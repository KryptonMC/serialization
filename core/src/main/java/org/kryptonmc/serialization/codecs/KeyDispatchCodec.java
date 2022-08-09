/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.codecs;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.Decoder;
import org.kryptonmc.serialization.Encoder;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;

public final class KeyDispatchCodec<K, V> implements MapCodec<V> {

    private static final String VALUE_KEY = "value";

    public static <K, V> @NotNull KeyDispatchCodec<K, V> unsafe(final @NotNull String typeKey, final @NotNull Codec<K> keyCodec,
                                                                final @NotNull Function<? super V, ? extends K> type,
                                                                final @NotNull Function<? super K, ? extends Decoder<? extends V>> decoder,
                                                                final @NotNull Function<? super V, ? extends Encoder<V>> encoder) {
        return new KeyDispatchCodec<>(typeKey, keyCodec, type, decoder, encoder, true);
    }

    private final String typeKey;
    private final Codec<K> keyCodec;
    private final Function<? super V, ? extends K> type;
    private final Function<? super K, ? extends Decoder<? extends V>> decoder;
    private final Function<? super V, ? extends Encoder<V>> encoder;
    private final boolean assumeMap;

    public KeyDispatchCodec(final @NotNull String typeKey, final @NotNull Codec<K> keyCodec, final @NotNull Function<? super V, ? extends K> type,
                            final @NotNull Function<? super K, ? extends Codec<? extends V>> codec) {
        this(typeKey, keyCodec, type, codec, value -> getEncoder(type, codec, value), false);
    }

    private KeyDispatchCodec(final @NotNull String typeKey, final @NotNull Codec<K> keyCodec, final @NotNull Function<? super V, ? extends K> type,
                             final @NotNull Function<? super K, ? extends Decoder<? extends V>> decoder,
                             final @NotNull Function<? super V, ? extends Encoder<V>> encoder, boolean assumeMap) {
        this.typeKey = typeKey;
        this.keyCodec = keyCodec;
        this.type = type;
        this.decoder = decoder;
        this.encoder = encoder;
        this.assumeMap = assumeMap;
    }

    @Override
    public <T> @NotNull V decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var elementName = input.get(typeKey);
        if (elementName == null) throw new IllegalArgumentException("Input " + input + " does not contain required type key " + typeKey);
        final var key = keyCodec.decode(elementName, ops);
        final var elementDecoder = decoder.apply(key);
        if (elementDecoder instanceof final MapCodec.StandardCodec<?> standardCodec) {
            // noinspection unchecked
            return (V) standardCodec.codec().decode(input, ops);
        }
        if (assumeMap) return elementDecoder.decode(ops.createMap(input.entries()), ops);
        return elementDecoder.decode(Objects.requireNonNull(input.get(VALUE_KEY)), ops);
    }

    @Override
    public @NotNull <T> RecordBuilder<T> encode(final @NotNull V input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
        final var elementEncoder = encoder.apply(input);
        if (elementEncoder == null) return prefix;
        if (elementEncoder instanceof final MapCodec.StandardCodec<?> standardCodec) {
            // noinspection unchecked
            return ((MapCodec.StandardCodec<V>) standardCodec).codec().encode(input, ops, prefix)
                    .add(typeKey, keyCodec.encodeStart(type.apply(input), ops));
        }
        final var typeString = ops.createString(typeKey);
        final var element = elementEncoder.encodeStart(input, ops);
        if (assumeMap) {
            final var map = ops.getMap(element);
            prefix.add(typeString, keyCodec.encodeStart(type.apply(input), ops));
            map.entries().forEach(entry -> {
                if (!entry.first().equals(typeString)) prefix.add(entry.first(), entry.second());
            });
            return prefix;
        }
        prefix.add(typeString, keyCodec.encodeStart(type.apply(input), ops));
        prefix.add(VALUE_KEY, element);
        return prefix;
    }

    @Override
    public String toString() {
        return "KeyDispatchCodec[" + keyCodec.toString() + " " + type + " " + decoder + "]";
    }

    private static <K, V> Encoder<V> getEncoder(final @NotNull Function<? super V, ? extends K> type,
                                                final @NotNull Function<? super K, ? extends Encoder<? extends V>> encoder, final @NotNull V input) {
        // noinspection unchecked
        return (Encoder<V>) encoder.apply(type.apply(input));
    }
}
