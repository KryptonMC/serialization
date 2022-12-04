/*
 * This file is part of Krypton Serialization, and originates from the Data
 * Fixer Upper, licensed under the MIT license.
 *
 * Copyright (C) Microsoft Corporation. All rights reserved.
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 *
 * For the original file that this file is derived from, see here:
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/codecs/KeyDispatchCodec.java
 */
package org.kryptonmc.serialization.codecs;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kryptonmc.serialization.Codec;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.Decoder;
import org.kryptonmc.serialization.Encoder;
import org.kryptonmc.serialization.MapCodec;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

/**
 * A codec for a value that uses the value of a key in a map to determine what
 * codec to use when decoding the value in the map.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public final class KeyDispatchCodec<K, V> implements MapCodec<V> {

    private static final String VALUE_KEY = "value";

    /**
     * Creates a new key dispatch codec that is unsafe because it assumes that
     * the input to the encoder/decoder is a map, when it may not be.
     *
     * @param typeKey The key of the type field.
     * @param keyCodec The key codec.
     * @param type The function used to get the key for a given value.
     * @param decoder The function used to get the decoder for a given key.
     * @param encoder The function used to get the encoder for a given value.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return A new key dispatch codec.
     */
    public static <K, V> @NotNull KeyDispatchCodec<K, V> unsafe(
            final @NotNull String typeKey, final @NotNull Codec<K> keyCodec,
            final @NotNull Function<? super V, ? extends DataResult<? extends K>> type,
            final @NotNull Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> decoder,
            final @NotNull Function<? super V, ? extends DataResult<? extends Encoder<V>>> encoder) {
        return new KeyDispatchCodec<>(typeKey, keyCodec, type, decoder, encoder, true);
    }

    private final String typeKey;
    private final Codec<K> keyCodec;
    private final Function<? super V, ? extends DataResult<? extends K>> type;
    private final Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> decoder;
    private final Function<? super V, ? extends DataResult<? extends Encoder<V>>> encoder;
    private final boolean assumeMap;

    /**
     * Creates a new key dispatch codec that does not assume the input to the
     * encoder/decoder is a map.
     *
     * @param typeKey The key of the type field.
     * @param keyCodec The key codec.
     * @param type The function used to get the key for a given value.
     * @param codec The function used to get the codec used to encode/decode
     *              the value from a given key.
     */
    public KeyDispatchCodec(final @NotNull String typeKey, final @NotNull Codec<K> keyCodec,
                            final @NotNull Function<? super V, ? extends DataResult<? extends K>> type,
                            final @NotNull Function<? super K, ? extends DataResult<? extends Codec<? extends V>>> codec) {
        this(typeKey, keyCodec, type, codec, value -> getEncoder(type, codec, value), false);
    }

    private KeyDispatchCodec(final @NotNull String typeKey, final @NotNull Codec<K> keyCodec,
                             final @NotNull Function<? super V, ? extends DataResult<? extends K>> type,
                             final @NotNull Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> decoder,
                             final @NotNull Function<? super V, ? extends DataResult<? extends Encoder<V>>> encoder, final boolean assumeMap) {
        this.typeKey = Objects.requireNonNull(typeKey, "typeKey");
        this.keyCodec = Objects.requireNonNull(keyCodec, "keyCodec");
        this.type = Objects.requireNonNull(type, "type");
        this.decoder = Objects.requireNonNull(decoder, "decoder");
        this.encoder = Objects.requireNonNull(encoder, "encoder");
        this.assumeMap = assumeMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull DataResult<V> decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
        final var elementName = input.get(typeKey);
        if (elementName == null) return DataResult.error("Input " + input + " does not contain required type key " + typeKey);

        return keyCodec.decode(elementName, ops).flatMap(type -> {
            final var elementDecoder = decoder.apply(type.first());
            return elementDecoder.flatMap(decoder -> {
                if (decoder instanceof MapCodec.StandardCodec<?>) {
                    return (DataResult<V>) ((MapCodec.StandardCodec<? extends V>) decoder).codec().decode(input, ops);
                }
                if (assumeMap) return decoder.decode(ops.createMap(input.entries()), ops).map(Pair::first);
                return decoder.decode(input.get(VALUE_KEY), ops).map(Pair::first);
            });
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull RecordBuilder<T> encode(final @NotNull V input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
        final var elementEncoder = encoder.apply(input);
        final var builder = prefix.withErrorsFrom(elementEncoder);
        if (elementEncoder.result().isEmpty()) return builder;

        final var encoder = elementEncoder.result().get();
        if (encoder instanceof MapCodec.StandardCodec<?>) {
            return ((MapCodec.StandardCodec<V>) encoder).codec().encode(input, ops, prefix)
                    .add(typeKey, type.apply(input).flatMap(t -> keyCodec.encodeStart(t, ops)));
        }

        final var typeString = ops.createString(typeKey);
        final var result = encoder.encodeStart(input, ops);
        if (assumeMap) {
            final var element = result.flatMap(ops::getMap);
            return element.map(map -> {
                prefix.add(typeString, type.apply(input).flatMap(t -> keyCodec.encodeStart(t, ops)));
                map.entries().forEach(entry -> {
                    if (!entry.first().equals(typeString)) prefix.add(entry.first(), entry.second());
                });
                return prefix;
            }).result().orElseGet(() -> prefix.withErrorsFrom(element));
        }
        prefix.add(typeString, type.apply(input).flatMap(t -> keyCodec.encodeStart(t, ops)));
        prefix.add(VALUE_KEY, result);
        return prefix;
    }

    @Override
    public String toString() {
        return "KeyDispatchCodec[" + keyCodec.toString() + " " + type + " " + decoder + "]";
    }

    @SuppressWarnings("unchecked")
    private static <K, V> @NotNull DataResult<? extends Encoder<V>> getEncoder(
            final @NotNull Function<? super V, ? extends DataResult<? extends K>> type,
            final @NotNull Function<? super K, ? extends DataResult<? extends Encoder<? extends V>>> encoder,
            final @NotNull V input) {
        return type.apply(input).flatMap(k -> (DataResult<Encoder<V>>) encoder.apply(k));
    }
}
