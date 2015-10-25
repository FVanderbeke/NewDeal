package org.newdeal.core.dispatch;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 07/09/2015
 * @version 0.0.1
 */
public interface Updatable<A, B> {
    B copy(A updatedValue);
}
