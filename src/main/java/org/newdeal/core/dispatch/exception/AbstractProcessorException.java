package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.request.DispatchedRequest;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
abstract class AbstractProcessorException extends AbstractDispatchException {

    AbstractProcessorException(Referenceable processorRef) {
        super(processorRef);
    }

    AbstractProcessorException(DispatchedRequest request, Referenceable processorRef) {
        super(request, processorRef);
    }

    public Referenceable getProcessorRef() {
        return getOwnerRef();
    }
}
