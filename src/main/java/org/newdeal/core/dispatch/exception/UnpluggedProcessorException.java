package org.newdeal.core.dispatch.exception;

import org.newdeal.core.system.request.DispatchedRequest;

/**
 * -- UNSTABLE --
 *
 * Thrown when trying to use a processor that is active and declared but not plugged on a station used to dispatche the
 * request.
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class UnpluggedProcessorException extends AbstractProcessorException {
    public UnpluggedProcessorException(DispatchedRequest request) {
        super(request, request.getProcessorRef());
    }
}
