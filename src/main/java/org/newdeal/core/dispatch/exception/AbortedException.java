package org.newdeal.core.dispatch.exception;

import org.newdeal.core.system.request.DispatchedRequest;

/**
 * -- UNSTABLE --
 * <p>
 * Thrown when a request is abandoned during the accepting processing step.
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
public class AbortedException extends AbstractAbandonedException {
    private final DispatchedRequest origin;

    public AbortedException(DispatchedRequest origin) {
        this.origin = origin;
    }

    public DispatchedRequest getRequest() {
        return origin;
    }

}
