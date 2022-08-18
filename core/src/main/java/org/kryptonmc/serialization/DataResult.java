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
 * https://github.com/Mojang/DataFixerUpper/blob/c100ef03c2ab321e5de4f25ffbe277e924aa7ca5/src/main/java/com/mojang/serialization/DataResult.java
 */
package org.kryptonmc.serialization;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.kryptonmc.util.Either;
import org.kryptonmc.util.function.Function3;
import org.kryptonmc.util.functional.App;
import org.kryptonmc.util.functional.Applicative;
import org.kryptonmc.util.functional.K1;

/**
 * A result of doing an action with some data.
 *
 * <p>This class is modelled after the very common {@code Result} type in many
 * functional programming languages, and represents either a successful result,
 * or an error message with an optional partial result (which can provide a
 * half complete result, indicating what the result was when the error
 * occurred).</p>
 *
 * @param <R> The result type.
 * @see <a href="https://en.wikipedia.org/wiki/Result_type">Result Type</a>
 */
public final class DataResult<R> implements App<DataResult.Mu, R> {

    /**
     * Creates a new data result that represents a successful result with the
     * given result.
     *
     * @param result The result.
     * @param <R> The result type.
     * @return A new successful data result.
     */
    public static <R> @NotNull DataResult<R> success(final @NotNull R result) {
        return success(result, Lifecycle.experimental());
    }

    /**
     * Creates a new data result that represents a successful result with the
     * given result and lifecycle.
     *
     * @param result The result.
     * @param lifecycle The lifecycle.
     * @param <R> The result type.
     * @return A new successful data result.
     */
    public static <R> @NotNull DataResult<R> success(final @NotNull R result, final @NotNull Lifecycle lifecycle) {
        return create(Either.left(result), lifecycle);
    }

    /**
     * Creates a new data result that represents an error result with the given
     * error message.
     *
     * @param message The error message.
     * @param <R> The result type.
     * @return A new error data result.
     */
    public static <R> @NotNull DataResult<R> error(final @NotNull String message) {
        return error(message, Lifecycle.experimental());
    }

    /**
     * Creates a new data result that represents an error result with the given
     * error message and partial result that indicates the result before the
     * error occurred.
     *
     * @param message The error message.
     * @param partialResult The partial result.
     * @param <R> The result type.
     * @return A new error data result.
     */
    public static <R> @NotNull DataResult<R> error(final @NotNull String message, final @NotNull R partialResult) {
        return error(message, partialResult, Lifecycle.experimental());
    }

    /**
     * Creates a new data result that represents an error result with the given
     * error message, lifecycle, and partial result that indicates the result
     * before the error occurred.
     *
     * @param message The error message.
     * @param partialResult The partial result.
     * @param lifecycle The lifecycle.
     * @param <R> The result type.
     * @return A new error data result.
     */
    public static <R> @NotNull DataResult<R> error(final @NotNull String message, final @NotNull R partialResult,
                                                   final @NotNull Lifecycle lifecycle) {
        return create(Either.right(new PartialResult<>(message, Optional.of(partialResult))), lifecycle);
    }

    /**
     * Creates a new data result that represents an error result with the given
     * error message and lifecycle.
     *
     * @param message The error message.
     * @param lifecycle The lifecycle.
     * @param <R> The result type.
     * @return A new error data result.
     */
    public static <R> @NotNull DataResult<R> error(final @NotNull String message, final @NotNull Lifecycle lifecycle) {
        return create(Either.right(new PartialResult<>(message, Optional.empty())), lifecycle);
    }

    /**
     * Gets the instance for the data result applicative.
     *
     * @return The instance for the data result applicative.
     */
    public static @NotNull Instance instance() {
        return Instance.INSTANCE;
    }

    @VisibleForTesting
    static <R> @NotNull DataResult<R> create(final @NotNull Either<R, PartialResult<R>> result, final @NotNull Lifecycle lifecycle) {
        return new DataResult<>(result, lifecycle);
    }

    private static <R> @NotNull DataResult<R> unbox(final @NotNull App<Mu, R> box) {
        return (DataResult<R>) box;
    }

    private final Either<R, PartialResult<R>> result;
    private final Lifecycle lifecycle;

    private DataResult(final @NotNull Either<R, PartialResult<R>> result, final @NotNull Lifecycle lifecycle) {
        this.result = result;
        this.lifecycle = lifecycle;
    }

    /**
     * Gets the backing result contained within this data result.
     *
     * @return The backing result.
     */
    public @NotNull Either<R, PartialResult<R>> get() {
        return result;
    }

    /**
     * Gets the successful result, if present, from the backing result
     * contained within this data result.
     *
     * @return The successful result, if present.
     */
    public @NotNull Optional<R> result() {
        return result.left();
    }

    /**
     * Gets the error result, if present, from the backing result contained
     * within this data result.
     *
     * @return The error result, if present.
     */
    public @NotNull Optional<PartialResult<R>> error() {
        return result.right();
    }

    /**
     * Gets the lifecycle of this data result.
     *
     * @return The lifecycle.
     */
    public @NotNull Lifecycle lifecycle() {
        return lifecycle;
    }

    /**
     * Gets the successful result, if present, from the backing result
     * contained within this data result, or returns the partial result, if
     * present, calling the given on error handler if the partial result is
     * present (this result is an error result).
     *
     * @param onError The on error handler.
     * @return The successful result, if present, or the partial result, if
     *         present.
     */
    public @NotNull Optional<R> resultOrPartial(final @NotNull Consumer<String> onError) {
        return result.map(Optional::of, partial -> {
            onError.accept(partial.message);
            return partial.partialResult;
        });
    }

    /**
     * Gets the successful result, if present, from the backing result
     * contained within this data result, or, if present, returns the partial
     * result, if allowPartial is true.
     *
     * <p>If this result is an error result, the on error handler will be
     * called, and, if allowPartial is true and the partial result is present,
     * the partial result will be returned, else a {@link RuntimeException}
     * will be thrown with the message contained within the partial result.</p>
     *
     * @param allowPartial If the partial result is allowed to be returned,
     *                     should the successful result be absent and the
     *                     partial result be present.
     * @param onError The on error handler.
     * @return The result.
     * @throws RuntimeException If this result is an error result and either
     *                          allowPartial is false, or allowPartial is true
     *                          and the partial result is absent.
     */
    public @NotNull R getOrThrow(final boolean allowPartial, final @NotNull Consumer<String> onError) {
        return result.map(Function.identity(), partial -> {
            onError.accept(partial.message);
            if (allowPartial && partial.partialResult.isPresent()) return partial.partialResult.get();
            throw new RuntimeException(partial.message);
        });
    }

    /**
     * Maps this data result to a new result, applying the given mapper to the
     * successful result, if present, or the partial result, if present.
     *
     * @param mapper The mapper to apply to the successful or partial result.
     * @param <R2> The new result type.
     * @return The resulting data result.
     */
    public <R2> @NotNull DataResult<R2> map(final @NotNull Function<? super R, ? extends R2> mapper) {
        return create(result.mapBoth(mapper, partial -> new PartialResult<>(partial.message, partial.partialResult.map(mapper))), lifecycle);
    }

    /**
     * Promotes the partial result contained within this data result if it is
     * an error result, returning a new data result with the partial result as
     * the successful result if it is present, or the partial result as the
     * error result if it is not.
     *
     * <p>The error contained within the partial result will be provided by
     * calling the given on error handler.</p>
     *
     * @param onError The on error handler.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> promotePartial(final @NotNull Consumer<String> onError) {
        // Optimization: Avoid unnecessarily recreating the data result.
        // In the original code, for the left half, when the result is left, it creates a new data result with a new left either wrapping the
        // successful result, using this result's lifecycle. However, if, when we call map, it calls the left mapper, we know the either is left,
        // and whatever value we were provided was the left value, so we can just return this result, as it's the same thing.
        // For the right half, when the result is right, and the partial result is not present, it creates a new data result with a new right
        // either wrapping the partial result, using this result's lifecycle. However, if, when we call map, it calls the right mapper, we know
        // the either is right, and whatever value we were provided was the right value, so we can just return this result, as it's the same thing.
        return result.map(value -> this, partial -> {
            onError.accept(partial.message);
            return partial.partialResult.map(value -> create(Either.left(value), lifecycle)).orElse(this);
        });
    }

    private static @NotNull String appendMessages(final @NotNull String first, final @NotNull String second) {
        return first + "; " + second;
    }

    /**
     * Maps this data result to a new result, applying the given mapper to the
     * successful result, if present, or the partial result, if present.
     *
     * <p>This differs from {@link #map(Function)} in that the function here
     * returns a {@link DataResult}, which allows errors and other information
     * to be carried from other results in to a new result mapped from this
     * one.</p>
     *
     * @param mapper The mapper to apply to the successful or partial result.
     * @param <R2> The new result type.
     * @return The resulting data result.
     */
    public <R2> @NotNull DataResult<R2> flatMap(final @NotNull Function<? super R, ? extends DataResult<R2>> mapper) {
        final Function<? super R, ? extends DataResult<R2>> leftMapper = value -> {
            final DataResult<R2> second = mapper.apply(value);
            return create(second.get(), lifecycle.add(second.lifecycle));
        };
        final Function<? super PartialResult<R>, ? extends DataResult<R2>> rightMapper = partial -> partial.partialResult
                .map(value -> {
                    final DataResult<R2> second = mapper.apply(value);
                    return create(Either.right(second.get().map(
                            left -> new PartialResult<>(partial.message, Optional.of(left)),
                            right -> new PartialResult<>(appendMessages(partial.message, right.message), right.partialResult)
                    )), lifecycle.add(second.lifecycle));
                })
                .orElseGet(() -> create(Either.right(new PartialResult<>(partial.message, Optional.empty())), lifecycle));
        return result.map(leftMapper, rightMapper);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public <R2> @NotNull DataResult<R2> ap(final @NotNull DataResult<Function<R, R2>> functionResult) {
        return create(result.map(
                argument -> functionResult.result.mapBoth(
                        function -> function.apply(argument),
                        functionError -> new PartialResult<>(functionError.message, functionError.partialResult.map(f -> f.apply(argument)))
                ),
                argumentError -> Either.right(functionResult.result.map(
                        function -> new PartialResult<>(argumentError.message, argumentError.partialResult.map(function)),
                        functionError -> new PartialResult<>(
                                appendMessages(argumentError.message, functionError.message),
                                argumentError.partialResult.flatMap(a -> functionError.partialResult.map(f -> f.apply(a)))
                        )
                ))
        ), lifecycle.add(functionResult.lifecycle));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public <R2, S> @NotNull DataResult<S> apply2(final @NotNull BiFunction<R, R2, S> function, final @NotNull DataResult<R2> second) {
        return unbox(instance().apply2(function, this, second));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public <R2, S> @NotNull DataResult<S> apply2stable(final @NotNull BiFunction<R, R2, S> function, final @NotNull DataResult<R2> second) {
        return unbox(instance().ap2(unbox(instance().point(function)).withLifecycle(Lifecycle.stable()), this, second));
    }

    /**
     * Maps this data result to a new result, with the result of getting the
     * given partial supplier applied to the partial result, if any.
     *
     * @param partial The new partial result supplier.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> withPartial(final @NotNull Supplier<R> partial) {
        // Optimization: If the result is not right, we don't need to recreate the data result, as our result is successful and doesn't
        // have an existing partial result to change.
        if (result.right().isEmpty()) return this;
        return create(result.mapRight(right -> new PartialResult<>(right.message, Optional.of(partial.get()))), lifecycle);
    }

    /**
     * Maps this data result to a new result, with the given partial value as
     * the new partial result, if this data result already has a partial
     * result.
     *
     * @param partial The new partial result.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> withPartial(final @NotNull R partial) {
        // Optimization: If the result is not right, we don't need to recreate the data result, as our result is successful and doesn't
        // have an existing partial result to change.
        if (result.right().isEmpty()) return this;
        return create(result.mapRight(right -> new PartialResult<>(right.message, Optional.of(partial))), lifecycle);
    }

    /**
     * Maps the error message, if any, contained within this data result with
     * the given mapper.
     *
     * @param mapper The mapper to apply to the error message.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> mapError(final @NotNull UnaryOperator<String> mapper) {
        // Optimization: If the result is not right, we don't need to recreate the data result, as our result is successful and doesn't
        // have an existing partial result to map the error message of.
        if (result.right().isEmpty()) return this;
        return create(result.mapRight(right -> new PartialResult<>(mapper.apply(right.message), right.partialResult)), lifecycle);
    }

    /**
     * Sets the lifecycle of the data result to the given lifecycle, returning
     * the resulting data result.
     *
     * <p>If the existing lifecycle of this result is the same as the provided
     * lifecycle, this method will simply return this result.</p>
     *
     * @param lifecycle The new lifecycle.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> withLifecycle(final @NotNull Lifecycle lifecycle) {
        // Optimization: If the provided lifecycle is the same as the one we already have, don't recreate the data result.
        if (this.lifecycle.equals(lifecycle)) return this;
        return create(result, lifecycle);
    }

    /**
     * Adds the given lifecycle to this result's lifecycle and returns the
     * resulting data result with its lifecycle being the union of the two.
     *
     * <p>If the resulting added lifecycle is the same as the existing
     * lifecycle, this method will simply return this data result.</p>
     *
     * @param lifecycle The lifecycle to add.
     * @return The resulting data result.
     */
    public @NotNull DataResult<R> addLifecycle(final @NotNull Lifecycle lifecycle) {
        // Optimization: We add the lifecycles early and check if the existing lifecycle is the same as the new one, because if it is,
        // we don't need to recreate the data result.
        final var newLifecycle = this.lifecycle.add(lifecycle);
        if (this.lifecycle.equals(newLifecycle)) return this;
        return create(result, newLifecycle);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(result, ((DataResult<?>) o).result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        return "DataResult[" + result + ']';
    }

    /**
     * A partial result that represents an error that occurred while trying to
     * perform an action.
     *
     * <p>This is a wrapper that encompasses an error message, explaining what
     * actually happened to cause the error to occur, and optionally, a partial
     * result, which should be the result that would be returned if execution
     * was halted at the point of the error occurring.</p>
     *
     * @param message The error message.
     * @param partialResult The partial result, if any.
     * @param <R> The result type.
     */
    public record PartialResult<R>(@NotNull String message, @NotNull Optional<R> partialResult) {

        /**
         * Maps this partial result, applying the given mapper to the partial
         * result contained within this partial result, if it is present.
         *
         * @param mapper The mapper to apply to the contained partial result.
         * @param <R2> The new result type.
         * @return The resulting partial result.
         */
        public <R2> @NotNull PartialResult<R2> map(final @NotNull Function<? super R, ? extends R2> mapper) {
            return new PartialResult<>(message, partialResult.map(mapper));
        }

        /**
         * Maps this partial result, applying the given mapper to the partial
         * result contained within this partial result, if it is present.
         *
         * <p>This differs from {@link #map(Function)} in that the function here
         * returns a {@link PartialResult}, which allows errors and other information
         * to be carried from other results in to a new result mapped from this
         * one.</p>
         *
         * @param mapper The mapper to apply to the contained partial result.
         * @param <R2> The new result type.
         * @return The resulting partial result.
         */
        @SuppressWarnings("unchecked")
        public <R2> @NotNull PartialResult<R2> flatMap(final @NotNull Function<R, PartialResult<R2>> mapper) {
            if (partialResult.isPresent()) {
                final PartialResult<R2> result = mapper.apply(partialResult.get());
                return new PartialResult<>(appendMessages(message, result.message), result.partialResult);
            }
            // Optimization: If the partial result is not present, we know that it's Optional.empty(), which is a constant, so we will be
            // returning the same as creating a new partial result with the result set to Optional.empty().
            // It's safe to cast the result here, as we know that the result is Optional.empty(), which means we don't care what the generic is.
            return (PartialResult<R2>) this;
        }

        @Override
        public String toString() {
            return "DynamicException[" + message + ' ' + partialResult + ']';
        }
    }

    /**
     * A mu for the data result applicative.
     */
    public static final class Mu implements K1 {

        private Mu() {
        }
    }

    /**
     * An instance for the data result applicative.
     */
    public enum Instance implements Applicative<Mu, Instance.Mu> {

        INSTANCE;

        @Override
        public <T, R> @NotNull App<DataResult.Mu, R> map(final @NotNull Function<? super T, ? extends R> function,
                                                         final @NotNull App<DataResult.Mu, T> argument) {
            return unbox(argument).map(function);
        }

        @Override
        public <A> @NotNull App<DataResult.Mu, A> point(final @NotNull A a) {
            return success(a);
        }

        @Override
        public <A, R> @NotNull Function<App<DataResult.Mu, A>, App<DataResult.Mu, R>> lift1(
                final @NotNull App<DataResult.Mu, Function<A, R>> function) {
            return fa -> ap(function, fa);
        }

        @Override
        public <A, R> @NotNull App<DataResult.Mu, R> ap(final @NotNull App<DataResult.Mu, Function<A, R>> function,
                                                        final @NotNull App<DataResult.Mu, A> argument) {
            return unbox(argument).ap(unbox(function));
        }

        @Override
        public <A, B, R> @NotNull App<DataResult.Mu, R> ap2(final @NotNull App<DataResult.Mu, BiFunction<A, B, R>> function,
                                                            final @NotNull App<DataResult.Mu, A> a, final @NotNull App<DataResult.Mu, B> b) {
            final var fr = unbox(function);
            final var ra = unbox(a);
            final var rb = unbox(b);

            // Optimization: Avoid recursion for the common case where the function and all the results are a success.
            if (fr.result.left().isPresent() && ra.result.left().isPresent() && rb.result.left().isPresent()) {
                return create(
                        Either.left(fr.result.left().get().apply(ra.result.left().get(), rb.result.left().get())),
                        fr.lifecycle.add(ra.lifecycle).add(rb.lifecycle)
                );
            }
            return Applicative.super.ap2(function, a, b);
        }

        @Override
        public <A, B, C, R> @NotNull App<DataResult.Mu, R> ap3(
                final @NotNull App<DataResult.Mu, Function3<A, B, C, R>> function, final @NotNull App<DataResult.Mu, A> a,
                final @NotNull App<DataResult.Mu, B> b, final @NotNull App<DataResult.Mu, C> c) {
            final var fr = unbox(function);
            final var ra = unbox(a);
            final var rb = unbox(b);
            final var rc = unbox(c);

            // Optimization: Avoid recursion for the common case where the function and all the results are a success.
            if (fr.result.left().isPresent() && ra.result.left().isPresent() && rb.result.left().isPresent() && rc.result.left().isPresent()) {
                return create(
                        Either.left(fr.result.left().get().apply(ra.result.left().get(), rb.result.left().get(), rc.result.left().get())),
                        fr.lifecycle.add(ra.lifecycle).add(rb.lifecycle).add(rc.lifecycle)
                );
            }
            return Applicative.super.ap3(function, a, b, c);
        }

        /**
         * A mu for the data result instance.
         */
        public static final class Mu implements Applicative.Mu {

            private Mu() {
            }
        }
    }
}
