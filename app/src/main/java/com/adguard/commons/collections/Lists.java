/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.commons.collections;

import com.adguard.commons.collections.predicates.Predicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * List utils
 */
public class Lists {

    /**
     * Safe method to remove an element from collection.
     * If either collection is empty or element is null - returns false.
     *
     * @param collection Collection
     * @param element    Element
     * @param <T>        Any type
     * @return true if element has been removed
     */
    public static <T> boolean remove(Collection<T> collection, T element) {

        if (element == null) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (CollectionUtils.isEmpty(collection)) {
            return false;
        }

        return collection.remove(element);
    }

    /**
     * Safe method to remove all specified elements
     * from the collection.
     *
     * @param collection Collection
     * @param elements   Elements to remove
     * @param <T>        Any type
     */
    public static <T> void removeAll(Collection<T> collection, Collection<T> elements) {

        if (CollectionUtils.isEmpty(collection) || CollectionUtils.isEmpty(elements)) {
            return;
        }

        for (T element : elements) {
            collection.remove(element);
        }
    }

    /**
     * Checks if collection contains element for which predicate evaluates to true.s
     *
     * @param collection Collection
     * @param predicate  Predicate to check
     * @param <T>        Any type
     * @return true if at least one element exists
     */
    public static <T> boolean exists(Collection<T> collection, Predicate<T> predicate) {

        if (CollectionUtils.isEmpty(collection)) {
            return false;
        }

        if (predicate == null) {
            return false;
        }

        for (T element : collection) {
            if (predicate.evaluate(element)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Partitions list into smaller lists of the specified size
     *
     * @param input Input collection
     * @param size  Partition size
     * @return List of smaller size
     */
    public static <E> List<List<E>> split(Collection<E> input, int size) {
        List<List<E>> master = new ArrayList<>();
        if (input != null && input.size() > 0) {
            List<E> col = new ArrayList<>(input);
            boolean done = false;
            int startIndex = 0;
            int endIndex = col.size() > size ? size : col.size();
            while (!done) {
                master.add(col.subList(startIndex, endIndex));
                if (endIndex == col.size()) {
                    done = true;
                } else {
                    startIndex = endIndex;
                    endIndex = col.size() > (endIndex + size) ? (endIndex + size) : col.size();
                }
            }
        }
        return master;
    }
}
