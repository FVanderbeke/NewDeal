package org.newdeal.core.dispatch;

import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.SubmittedRequest;

import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
class HandledProcessStep {
    private final HandledProcessState state;
    private final SubmittedRequest request;
    private final CompletableFuture<? extends Request> task;

    public HandledProcessStep(HandledProcessState state, SubmittedRequest request, CompletableFuture<? extends Request> task) {
        this.state = state;
        this.request = request;
        this.task = task;
    }

    public HandledProcessState getState() {
        return state;
    }

    public SubmittedRequest getRequest() {
        return request;
    }

    public CompletableFuture<? extends Request> getTask() {
        return task;
    }
}
