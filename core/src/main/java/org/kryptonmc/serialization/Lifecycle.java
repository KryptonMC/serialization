package org.kryptonmc.serialization;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates the lifecycle of an object or process, such as a codec, or a data
 * result.
 */
public class Lifecycle {

    private static final Lifecycle STABLE = new Lifecycle() {
        @Override
        public boolean equals(final Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return "Stable";
        }
    };
    private static final Lifecycle EXPERIMENTAL = new Lifecycle() {
        @Override
        public boolean equals(final Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return "Experimental";
        }
    };

    /**
     * Gets the stable lifecycle.
     *
     * @return The stable lifecycle.
     */
    public static @NotNull Lifecycle stable() {
        return STABLE;
    }

    /**
     * Gets the experimental lifecycle.
     *
     * @return The experimental lifecycle.
     */
    public static @NotNull Lifecycle experimental() {
        return EXPERIMENTAL;
    }

    /**
     * Creates a new deprecated lifecycle that is deprecated since the given
     * since version.
     *
     * @param since The since version.
     * @return A new deprecated lifecycle.
     */
    public static @NotNull Lifecycle deprecated(final int since) {
        return new Deprecated(since);
    }

    private Lifecycle() {
    }

    /**
     * Adds the given lifecycle to this one.
     *
     * <p>This will <b>never</b> create a new object. Instead, it will return
     * existing lifecycles, depending on what this lifecycle is, and what the
     * other lifecycle is. The following points are in order of when they are
     * checked in this method.</p>
     * <ul>
     *     <li>If either lifecycles are {@link #experimental()}, the result is {@link #experimental()}</li>
     *     <li>If both lifecycles are deprecated and the since of the other is <b>less than</b> the since of this, the result is {@code other}.</li>
     *     <li>If the above check failed, and this lifecycle is deprecated, the result is {@code this}.</li>
     *     <li>If the other lifecycle is deprecated, the result is {@code other}.</li>
     *     <li>If no other check above passes, the result is {@link #stable()}.</li>
     * </ul>
     *
     * @param other The lifecycle to add.
     * @return The resulting lifecycle.
     */
    public @NotNull Lifecycle add(final @NotNull Lifecycle other) {
        if (this == EXPERIMENTAL || other == EXPERIMENTAL) return EXPERIMENTAL;
        if (this instanceof final Deprecated deprecated) {
            if (other instanceof final Deprecated otherDeprecated && otherDeprecated.since < deprecated.since) return other;
            return this;
        }
        if (other instanceof Deprecated) return other;
        return STABLE;
    }

    /**
     * A lifecycle implementation that represents a deprecation of the element
     * it is attached to, with a since value to indicate the version in which
     * the element was deprecated.
     */
    public static final class Deprecated extends Lifecycle {

        private final int since;

        /**
         * Creates a new deprecated lifecycle with the given since version.
         *
         * @param since The since version.
         */
        public Deprecated(final int since) {
            this.since = since;
        }

        /**
         * Gets the version in which the element this lifecycle is attached to
         * was deprecated.
         *
         * @return The since version.
         */
        public int since() {
            return since;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return since == ((Deprecated) o).since;
        }

        @Override
        public int hashCode() {
            return Objects.hash(since);
        }

        @Override
        public String toString() {
            return "Deprecated[" + since + "]";
        }
    }
}
