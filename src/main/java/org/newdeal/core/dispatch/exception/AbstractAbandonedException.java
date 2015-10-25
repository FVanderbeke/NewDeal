package org.newdeal.core.dispatch.exception;

/**
 * -- UNSTABLE --
 * <p>
 * Marker used to recognize an exception that has been thrown because of a request cancelling query.
 * <p>
 * This kind of exception does not possess any stack (for performance purpose)
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
abstract class AbstractAbandonedException extends Exception {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
