package org.newdeal.core.dispatch.exception;

import org.newdeal.core.system.request.SubmittedRequest;

/**
 * -- UNSTABLE --
 *
 * Exception thrown when a request is abandoned before having being accepted by a processor (and during the waiting time)
 *
 * @author Frederick Vanderbeke
 * @since 18/09/2015
 * @version 0.0.1
 */
public class RenegedException extends AbstractAbandonedException {
    /**
     * Snapshot of request.
     */
    private final SubmittedRequest origin;

    public RenegedException(SubmittedRequest request) {
        this.origin = request;
    }

    public SubmittedRequest getRequest() {
        return origin;
    }
}
