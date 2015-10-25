package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;

/**
 * -- UNSTABLE --
 *
 * Thrown when trying to use an unknown processor (not declared on the dispatch engine).
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class UnknownProcessorException extends AbstractProcessorException {

    public UnknownProcessorException(Referenceable processorRef) {
        super(processorRef);
    }
}
