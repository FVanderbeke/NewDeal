package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.request.DispatchedRequest;

/**
 * -- UNSTABLE --
 *
 * Thrown when trying to use an inactive (but declared) processor.
 *
 * @author Frederick Vanderbeke
 * @since 17/08/2015
 * @version 0.0.1
 */
public class InactiveProcessorException extends AbstractDispatchException {
    public InactiveProcessorException(Referenceable processorRef) {
        super(processorRef);
    }

    public InactiveProcessorException(DispatchedRequest request) {
        super(request, request.getProcessorRef());
    }
}
