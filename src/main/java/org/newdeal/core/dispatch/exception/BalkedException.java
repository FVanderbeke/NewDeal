package org.newdeal.core.dispatch.exception;

import org.newdeal.core.system.request.SubmittedRequest;

/**
 * -- UNSTABLE --
 *
 * Used in Control-flow. So optimized to not generate stack trace
 *
 * @author Frederick Vanderbeke
 * @since 13/08/2015
 * @version 0.0.1
 */
public class BalkedException extends Exception {
    private final SubmittedRequest request;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public BalkedException(SubmittedRequest request) {
        this.request = request;
    }

    public SubmittedRequest getRequest() {
        return request;
    }
}
