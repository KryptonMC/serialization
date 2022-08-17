/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.nbt.ByteArrayTag;
import org.kryptonmc.nbt.ByteTag;
import org.kryptonmc.nbt.CollectionTag;
import org.kryptonmc.nbt.CompoundTag;
import org.kryptonmc.nbt.DoubleTag;
import org.kryptonmc.nbt.EndTag;
import org.kryptonmc.nbt.FloatTag;
import org.kryptonmc.nbt.IntArrayTag;
import org.kryptonmc.nbt.IntTag;
import org.kryptonmc.nbt.ListTag;
import org.kryptonmc.nbt.LongArrayTag;
import org.kryptonmc.nbt.LongTag;
import org.kryptonmc.nbt.NumberTag;
import org.kryptonmc.nbt.ShortTag;
import org.kryptonmc.nbt.StringTag;
import org.kryptonmc.nbt.Tag;
import org.kryptonmc.serialization.DataOps;
import org.kryptonmc.serialization.DataResult;
import org.kryptonmc.serialization.MapLike;
import org.kryptonmc.serialization.RecordBuilder;
import org.kryptonmc.util.Pair;

/**
 * The standard data operations for the Krypton NBT library.
 */
public final class NbtOps implements DataOps<Tag> {

    public static @NotNull NbtOps INSTANCE = new NbtOps();

    private NbtOps() {
    }

    @Override
    public @NotNull Tag empty() {
        return EndTag.INSTANCE;
    }

    @Override
    public @NotNull DataResult<Number> getNumberValue(final @NotNull Tag input) {
        return input instanceof final NumberTag tag ? DataResult.success(tag.asNumber()) : error("getNumberValue", "number", input);
    }

    @Override
    public @NotNull DataResult<String> getStringValue(final @NotNull Tag input) {
        return input instanceof final StringTag tag ? DataResult.success(tag.asString()) : error("getStringValue", "string", input);
    }

    @Override
    public @NotNull DataResult<Stream<Tag>> getStream(final @NotNull Tag input) {
        return input instanceof final CollectionTag<?> tag ? DataResult.success(tag.stream().map(v -> v)) : error("getStream", "list", input);
    }

    @Override
    public @NotNull DataResult<Consumer<Consumer<Tag>>> getList(final @NotNull Tag input) {
        if (input instanceof final CollectionTag<?> tag) return DataResult.success(tag::forEach);
        return error("getList", "list", input);
    }

    /*
     * This implementation and the one below it may look a bit weird, but it's designed carefully to be as consistent will vanilla
     * Minecraft as possible.
     *
     * Unlike vanilla NBT, Krypton NBT doesn't have a CollectionTag, as it wouldn't really work properly due to the immutable list tag.
     * Therefore, we have to do this mess, which checks for each type individually (list and all the arrays are CollectionTags in vanilla).
     */
    @Override
    public @NotNull DataResult<Tag> mergeToList(final @NotNull Tag list, final @NotNull Tag value) {
        if (!(list instanceof CollectionTag<?>) && !(list instanceof EndTag)) {
            return DataResult.error("Cannot merge value " + value + " in to non-list " + list + "!");
        }
        final var result = NbtUtil.createGenericList(list, value.getId());
        NbtUtil.fillOne(result, list, value);
        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<Tag> mergeToList(final @NotNull Tag list, final @NotNull List<Tag> values) {
        if (!(list instanceof CollectionTag<?>) && !(list instanceof EndTag)) {
            return DataResult.error("Cannot merge values " + values + " in to non-list " + list + "!");
        }
        final var result = NbtUtil.createGenericList(list, values.stream().findFirst().map(Tag::getId).orElse(EndTag.ID));
        NbtUtil.fillMany(result, list, values);
        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<ByteBuffer> getByteBuffer(final @NotNull Tag input) {
        return input instanceof final ByteArrayTag tag ? DataResult.success(ByteBuffer.wrap(tag.getData())) : DataOps.super.getByteBuffer(input);
    }

    @Override
    public @NotNull DataResult<IntStream> getIntStream(final @NotNull Tag input) {
        return input instanceof final IntArrayTag tag ? DataResult.success(Arrays.stream(tag.getData())) : DataOps.super.getIntStream(input);
    }

    @Override
    public @NotNull DataResult<LongStream> getLongStream(final @NotNull Tag input) {
        return input instanceof final LongArrayTag tag ? DataResult.success(Arrays.stream(tag.getData())) : DataOps.super.getLongStream(input);
    }

    @Override
    public @NotNull DataResult<Stream<Pair<Tag, Tag>>> getMapValues(final @NotNull Tag input) {
        if (!(input instanceof final CompoundTag tag)) return error("getMapValues", "compound", input);
        return DataResult.success(tag.keySet().stream().map(key -> Pair.of(createString(key), tag.get(key))));
    }

    @Override
    public @NotNull DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(final @NotNull Tag input) {
        if (!(input instanceof final CompoundTag tag)) return error("getMapEntries", "compound", input);
        return DataResult.success(consumer -> tag.keySet().forEach(key -> consumer.accept(createString(key), tag.get(key))));
    }

    @Override
    public @NotNull DataResult<MapLike<Tag>> getMap(final @NotNull Tag input) {
        if (!(input instanceof final CompoundTag tag)) return error("getMap", "compound", input);
        return DataResult.success(new MapLike<>() {
            @Override
            public @Nullable Tag get(final @NotNull Tag key) {
                return tag.get(key.asString());
            }

            @Override
            public @Nullable Tag get(final @NotNull String key) {
                return tag.get(key);
            }

            @Override
            public @NotNull Stream<Pair<Tag, Tag>> entries() {
                return tag.keySet().stream().map(key -> Pair.of(createString(key), tag.get(key)));
            }

            @Override
            public String toString() {
                return "MapLike[" + tag + "]";
            }
        });
    }

    @Override
    public @NotNull Tag remove(final @NotNull Tag input, final @NotNull String key) {
        if (!(input instanceof final CompoundTag tag)) return input;
        final var result = CompoundTag.immutableBuilder();
        tag.keySet().stream().filter(tagKey -> !Objects.equals(tagKey, key))
                .forEach(tagKey -> result.put(tagKey, Objects.requireNonNull(tag.get(tagKey))));
        return result.build();
    }

    @Override
    public @NotNull DataResult<Tag> mergeToMap(final @NotNull Tag map, final @NotNull Tag key, final @NotNull Tag value) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error("Cannot merge value " + value + " in to non-map " + map + "!");
        }
        if (!(key instanceof StringTag)) return DataResult.error("Key " + key + " for mergeToMap is not a string!");
        final var result = CompoundTag.immutableBuilder();
        if (map instanceof final CompoundTag tag) {
            tag.keySet().forEach(element -> result.put(element, Objects.requireNonNull(tag.get(element))));
        }
        result.put(key.asString(), value);
        return DataResult.success(result.build());
    }

    @Override
    public @NotNull DataResult<Tag> mergeToMap(final @NotNull Tag map, final @NotNull MapLike<Tag> values) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error("Cannot merge values " + values + " in to non-map " + map + "!");
        }
        final var result = CompoundTag.immutableBuilder();
        if (map instanceof final CompoundTag tag) tag.keySet().forEach(element -> result.put(element, Objects.requireNonNull(tag.get(element))));

        final var failed = new ArrayList<Tag>();
        values.entries().forEach(entry -> {
            final var key = entry.first();
            if (!(key instanceof StringTag)) {
                failed.add(key);
            } else {
                result.put(key.asString(), Objects.requireNonNull(entry.second()));
            }
        });

        if (!failed.isEmpty()) {
            return DataResult.error("Cannot merge values " + values + " in to map " + map + "! Keys " + failed + " are not strings!");
        }
        return DataResult.success(result.build());
    }

    @Override
    public @NotNull Tag createNumber(final @NotNull Number number) {
        return DoubleTag.of(number.doubleValue());
    }

    @Override
    public @NotNull Tag createBoolean(final boolean value) {
        return ByteTag.of(value);
    }

    @Override
    public @NotNull Tag createByte(final byte value) {
        return ByteTag.of(value);
    }

    @Override
    public @NotNull Tag createShort(final short value) {
        return ShortTag.of(value);
    }

    @Override
    public Tag createInt(final int value) {
        return IntTag.of(value);
    }

    @Override
    public Tag createLong(final long value) {
        return LongTag.of(value);
    }

    @Override
    public Tag createFloat(final float value) {
        return FloatTag.of(value);
    }

    @Override
    public Tag createDouble(final double value) {
        return DoubleTag.of(value);
    }

    @Override
    public @NotNull Tag createString(final @NotNull String value) {
        return StringTag.of(value);
    }

    @Override
    public @NotNull Tag createList(final @NotNull Stream<Tag> input) {
        final var iterator = Iterators.peekingIterator(input.iterator());
        if (!iterator.hasNext()) return ListTag.empty();
        final var first = iterator.peek();
        if (first instanceof ByteTag) {
            final var list = Lists.newArrayList(Iterators.transform(iterator, entry -> ((ByteTag) entry).getValue()));
            return new ByteArrayTag(list);
        }
        if (first instanceof IntTag) {
            final var list = Lists.newArrayList(Iterators.transform(iterator, entry -> ((IntTag) entry).getValue()));
            return new IntArrayTag(list);
        }
        if (first instanceof LongTag) {
            final var list = Lists.newArrayList(Iterators.transform(iterator, entry -> ((LongTag) entry).getValue()));
            return new LongArrayTag(list);
        }
        final var result = ListTag.immutableBuilder();
        while (iterator.hasNext()) {
            final var element = iterator.next();
            if (!(element instanceof EndTag)) result.add(element);
        }
        return result.build();
    }

    @Override
    public @NotNull Tag createByteList(final @NotNull ByteBuffer input) {
        return new ByteArrayTag(NbtUtil.toArray(input));
    }

    @Override
    public @NotNull Tag createIntList(final @NotNull IntStream input) {
        return new IntArrayTag(input.toArray());
    }

    @Override
    public @NotNull Tag createLongList(final @NotNull LongStream input) {
        return new LongArrayTag(input.toArray());
    }

    @Override
    public @NotNull Tag createMap(final @NotNull Stream<Pair<Tag, Tag>> map) {
        final var result = CompoundTag.immutableBuilder();
        map.forEach(entry -> result.put(Objects.requireNonNull(entry.first()).asString(), Objects.requireNonNull(entry.second())));
        return result.build();
    }

    @Override
    public @NotNull RecordBuilder<Tag> mapBuilder() {
        return new NbtRecordBuilder();
    }

    @Override
    public <U> @NotNull U convertTo(final @NotNull DataOps<U> outOps, final @NotNull Tag input) {
        return switch (input.getId()) {
            case EndTag.ID -> outOps.empty();
            case ByteTag.ID -> outOps.createByte(((NumberTag) input).toByte());
            case ShortTag.ID -> outOps.createShort(((NumberTag) input).toShort());
            case IntTag.ID -> outOps.createInt(((NumberTag) input).toInt());
            case LongTag.ID -> outOps.createLong(((NumberTag) input).toLong());
            case FloatTag.ID -> outOps.createFloat(((NumberTag) input).toFloat());
            case DoubleTag.ID -> outOps.createDouble(((NumberTag) input).toDouble());
            case ByteArrayTag.ID -> outOps.createByteList(ByteBuffer.wrap(((ByteArrayTag) input).getData()));
            case StringTag.ID -> outOps.createString(input.asString());
            case ListTag.ID -> convertList(outOps, input);
            case CompoundTag.ID -> convertMap(outOps, input);
            case IntArrayTag.ID -> outOps.createIntList(Arrays.stream(((IntArrayTag) input).getData()));
            case LongArrayTag.ID -> outOps.createLongList(Arrays.stream(((LongArrayTag) input).getData()));
            default -> throw new IllegalStateException("Unknown tag type " + input.getId() + "!");
        };
    }

    @Override
    public String toString() {
        return "NBT";
    }

    private static <R> @NotNull DataResult<R> error(final @NotNull String methodName, final @NotNull String name, final @NotNull Tag input) {
        return DataResult.error("Provided input " + input + " for " + methodName + " is not a " + name + "!");
    }
}
