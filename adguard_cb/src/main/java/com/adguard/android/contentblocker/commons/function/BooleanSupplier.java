package com.adguard.android.contentblocker.commons.function;

/**
 * <pre>
 * Represents a function that produces a {@code boolean} result.
 *
 * This is a functional interface
 * which functional method is {@link #get()}.</pre>
 */
@FunctionalInterface
public interface BooleanSupplier {

    /**
     * Gets a result
     *
     * @return the function result as boolean
     */
    boolean get();
}
