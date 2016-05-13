package com.adguard.commons.collections.predicates;

/**
 * Defines a functor interface implemented by classes that perform a predicate
 * test on an object.
 */
public interface Predicate<T> {

    /**
     * Use the specified parameter to perform a test that returns true or false.
     *
     * @param object the object to evaluate, should not be changed
     * @return true or false
     */
    boolean evaluate(T object);
}
