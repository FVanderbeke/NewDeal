package org.newdeal.core.dispatch;

import org.newdeal.core.dispatch.exception.AbortedException;
import org.newdeal.core.dispatch.exception.CancelledException;
import org.newdeal.core.dispatch.exception.RenegedException;
import org.newdeal.core.system.request.AcceptedRequest;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.util.function.Function;

/**
 * -- UNSTABLE --
 * <p>
 * Enumeration used to identify the current state of an handled process.
 * This enumeration also gives access to an exception constructor, used to
 * instantiate the correct exception instance when the process is cancelled
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
public enum HandledProcessState {
    SUBMITTED(HandledProcessState::renegedException),
    DISPATCHING(HandledProcessState::renegedException),
    ACCEPTING(HandledProcessState::abortedException),
    HANDSHAKING(HandledProcessState::cancelledException),
    PRE_PROCESSING(HandledProcessState::cancelledException),
    PROCESSING(HandledProcessState::cancelledException),
    POST_PROCESSING(HandledProcessState::cancelledException),
    TERMINATING(HandledProcessState::cancelledException);

    private final Function<SubmittedRequest, Exception> whenCancelled;

    HandledProcessState(Function<SubmittedRequest, Exception> whenCancelled) {
        this.whenCancelled = whenCancelled;
    }

    Exception applyWhenCancelled(SubmittedRequest currentRequest) {
        return this.whenCancelled.apply(currentRequest);
    }

    private static Exception renegedException(SubmittedRequest request) {
        return new RenegedException(request);
    }

    private static Exception abortedException(SubmittedRequest request) {
        if (request instanceof DispatchedRequest) {
            return new AbortedException((DispatchedRequest) request);
        } else {
            return renegedException(request);
        }
    }

    private static Exception cancelledException(SubmittedRequest request) {
        if (request instanceof AcceptedRequest) {
            return new CancelledException((AcceptedRequest) request);
        } else {
            return abortedException(request);
        }
    }

}
