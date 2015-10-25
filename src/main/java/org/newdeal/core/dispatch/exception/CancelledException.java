package org.newdeal.core.dispatch.exception;

import org.newdeal.core.system.request.AcceptedRequest;

/**
 * -- UNSTABLE --
 * <p>
 * Last kind of exception thrown when a request is abandoned : this one indicates that the request is interrupted/cancelled
 * during the processing step (when the accepted request is passed to a processor; from the handshaking step to the terminating one)
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
public class CancelledException extends AbstractAbandonedException {
    /**
     * Snapshot of the current request state.
     */
    private final AcceptedRequest origin;

    /**
     * Default constructor.
     *
     * @param origin request state snapshot.
     */
    public CancelledException(AcceptedRequest origin) {
        this.origin = origin;
    }

    /**
     * @return last snapshot that the system had from the cancelled request state.
     */
    public AcceptedRequest getRequest() {
        return origin;
    }
}
