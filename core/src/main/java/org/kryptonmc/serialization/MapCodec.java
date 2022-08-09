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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kryptonmc.util.Either;

public interface MapCodec<A> extends MapEncoder<A>, MapDecoder<A> {

    static <A> @NotNull MapCodec<A> of(final @NotNull MapEncoder<A> encoder, final @NotNull MapDecoder<A> decoder) {
        return of(encoder, decoder, () -> "MapCodec[" + encoder + " " + decoder + "]");
    }

    static <A> @NotNull MapCodec<A> of(final @NotNull MapEncoder<A> encoder, final @NotNull MapDecoder<A> decoder,
                                       final @NotNull Supplier<String> name) {
        return new MapCodec<A>() {
            @Override
            public <T> @NotNull A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                return decoder.decode(input, ops);
            }

            @Override
            public @NotNull <T> RecordBuilder<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                        final @NotNull RecordBuilder<T> prefix) {
                return encoder.encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return name.get();
            }
        };
    }

    static <A> @NotNull MapCodec<A> unit(final @NotNull A defaultValue) {
        return of(Encoder.empty(), Decoder.unit(defaultValue));
    }

    static <A> @NotNull MapCodec<A> unit(final @NotNull Supplier<A> defaultValue) {
        return of(Encoder.empty(), Decoder.unit(defaultValue));
    }

    @ApiStatus.NonExtendable
    default @NotNull Codec<A> codec() {
        return new StandardCodec<>(this);
    }

    @ApiStatus.NonExtendable
    default <B> @NotNull MapCodec<B> xmap(final @NotNull Function<? super A, ? extends B> to, final @NotNull Function<? super B, ? extends A> from) {
        return of(comap(from), map(to), () -> this + "[xmapped]");
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElse(final @NotNull A value, final @NotNull Consumer<Exception> onError) {
        return orElseGet(() -> value, onError, () -> "OrElse[" + onError + " " + value + "]");
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull Consumer<Exception> onError) {
        return orElseGet(value, onError, () -> "OrElseGet[" + onError + " " + value.get() + "]");
    }

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull Consumer<Exception> onError,
                                           final @NotNull Supplier<String> name) {
        return mapResult(new ResultFunction<A>() {
            @Override
            public <T> @NotNull A apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                        final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> {
                    onError.accept(error);
                    return value.get();
                });
            }

            @Override
            public @NotNull <T> RecordBuilder<T> coApply(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                         final @NotNull RecordBuilder<T> result, final @Nullable Exception exception) {
                if (exception != null) onError.accept(exception);
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElse(final @NotNull A value) {
        return orElseGet(() -> value, () -> "OrElse[" + value + "]");
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value) {
        return orElseGet(value, () -> "OrElseGet[" + value.get() + "]");
    }

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> orElseGet(final @NotNull Supplier<? extends A> value, final @NotNull Supplier<String> name) {
        return mapResult(new ResultFunction<A>() {
            @Override
            public <T> @NotNull A apply(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops,
                                        final @NotNull Either<A, Exception> resultOrError) {
                return resultOrError.map(Function.identity(), error -> value.get());
            }

            @Override
            public @NotNull <T> RecordBuilder<T> coApply(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                         final @NotNull RecordBuilder<T> result, final @Nullable Exception exception) {
                return result;
            }

            @Override
            public String toString() {
                return name.get();
            }
        });
    }

    @ApiStatus.NonExtendable
    default @NotNull MapCodec<A> mapResult(final @NotNull ResultFunction<A> function) {
        return new MapCodec<A>() {
            @Override
            public <T> @NotNull A decode(final @NotNull MapLike<T> input, final @NotNull DataOps<T> ops) {
                try {
                    return function.apply(input, ops, Either.left(MapCodec.this.decode(input, ops)));
                } catch (final Exception exception) {
                    return function.apply(input, ops, Either.right(exception));
                }
            }

            @Override
            public @NotNull <T> RecordBuilder<T> encode(final @NotNull A input, final @NotNull DataOps<T> ops,
                                                        final @NotNull RecordBuilder<T> prefix) {
                try {
                    return function.coApply(input, ops, MapCodec.this.encode(input, ops, prefix), null);
                } catch (final Exception exception) {
                    return function.coApply(input, ops, prefix, exception);
                }
            }

            @Override
            public String toString() {
                return MapCodec.this + "[mapResult " + function + "]";
            }
        };
    }

    interface ResultFunction<A> {

        <T> @NotNull A apply(@NotNull MapLike<T> input, @NotNull DataOps<T> ops, @NotNull Either<A, Exception> resultOrError);

        <T> @NotNull RecordBuilder<T> coApply(@NotNull A input, @NotNull DataOps<T> ops, @NotNull RecordBuilder<T> result,
                                              @Nullable Exception exception);
    }

    record StandardCodec<A>(@NotNull MapCodec<A> codec) implements Codec<A> {

        @Override
        public <T> @NotNull A decode(final @NotNull T input, final @NotNull DataOps<T> ops) {
            return codec.decode(input, ops);
        }

        @Override
        public <T> @NotNull T encode(final @NotNull A input, final @NotNull DataOps<T> ops, final @NotNull T prefix) {
            return codec.encode(input, ops, ops.mapBuilder()).build(prefix);
        }

        @Override
        public String toString() {
            return codec.toString();
        }
    }
}
