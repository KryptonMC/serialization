/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
package org.kryptonmc.serialization;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.Either;

// Some private stuff that I would rather not expose with @ApiStatus.Internal
final class CodecUtil {

    public static MapEncoder<?> EMPTY = new MapEncoder<>() {
        @Override
        public @NotNull <T> RecordBuilder<T> encode(final Object input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> prefix) {
            return prefix;
        }

        @Override
        public String toString() {
            return "EmptyEncoder";
        }
    };

    public static <A> @NotNull Codec<A> orElseGet(final @NotNull Codec<A> codec, final @NotNull Supplier<A> value,
                                                  final @NotNull Consumer<Exception> onError, final @NotNull Supplier<String> name) {
        return codec.mapResult(new Codec.ResultFunction<>() {
            @Override
            public <T> A apply(final T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> {
                    onError.accept(error);
                    return value.get();
                });
            }

            @Override
            public <T> T coApply(final A input, final @NotNull DataOps<T> ops, final T result, final @Nullable Exception exception) {
                if (exception != null) onError.accept(exception);
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    public static <A> @NotNull Codec<A> orElseGet(final @NotNull Codec<A> codec, final @NotNull Supplier<A> value,
                                                  final @NotNull Supplier<String> name) {
        return codec.mapResult(new Codec.ResultFunction<>() {
            @Override
            public <T> A apply(final T input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> value.get());
            }

            @Override
            public <T> T coApply(final A input, final @NotNull DataOps<T> ops, final T result, final @Nullable Exception exception) {
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    public static <A> @NotNull MapCodec<A> orElseGet(final @NotNull MapCodec<A> codec, final @NotNull Supplier<? extends A> value,
                                                     final @NotNull Consumer<Exception> onError, final @NotNull Supplier<String> name) {
        return codec.mapResult(new MapCodec.ResultFunction<>() {
            @Override
            public <T> A apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> {
                    onError.accept(error);
                    return value.get();
                });
            }

            @Override
            public @NotNull <T> RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result,
                                                         final @Nullable Exception exception) {
                if (exception != null) onError.accept(exception);
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    public static <A> @NotNull MapCodec<A> orElseGet(final @NotNull MapCodec<A> codec, final @NotNull Supplier<? extends A> value,
                                                     final @NotNull Supplier<String> name) {
        return codec.mapResult(new MapCodec.ResultFunction<A>() {
            @Override
            public <T> A apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops, final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> value.get());
            }

            @Override
            public @NotNull <T> RecordBuilder<T> coApply(final A input, final @NotNull DataOps<T> ops, final @NotNull RecordBuilder<T> result,
                                                         final @Nullable Exception exception) {
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    private CodecUtil() {
    }
}
