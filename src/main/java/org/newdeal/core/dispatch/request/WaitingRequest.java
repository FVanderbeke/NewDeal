package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.PluggedProcessor;
import org.newdeal.core.dispatch.Updatable;
import org.newdeal.core.dispatch.exception.RenegedException;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * Wrapper class for submitted request that are waiting in a station
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class WaitingRequest extends AbstractComparableRequest implements SubmittedRequest, Updatable<SubmittedRequest, WaitingRequest> {
    private final SubmittedRequest origin;
    private final long ticket;
    private final CompletableFuture<DispatchedRequest> future;

    public WaitingRequest(SubmittedRequest origin, long ticket, CompletableFuture<DispatchedRequest> future) {
        this.origin = origin;
        this.ticket = ticket;
        this.future = future;
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    public void complete(PluggedProcessor processor) {
        future.complete(new DefaultDispatchedRequest(origin, ticket, processor));
    }

    public void cancel() {
        future.cancel(true);
    }

    @Override
    public Instant getHandleDate() {
        return origin.getHandleDate();
    }

    @Override
    public Instant getSubmissionDate() {
        return origin.getSubmissionDate();
    }

    @Override
    public Referenceable getStationRef() {
        return origin.getStationRef();
    }

    @Override
    public Clock getClock() {
        return origin.getClock();
    }

    @Override
    public Instant getCreationDate() {
        return origin.getCreationDate();
    }

    @Override
    public Optional<Request> getOrigin() {
        return origin.getOrigin();
    }

    public WaitingRequest copy(SubmittedRequest request) {
        return new WaitingRequest(request, ticket, future);
    }

    public void renege() {
        future.completeExceptionally(new RenegedException(origin));
    }
}
