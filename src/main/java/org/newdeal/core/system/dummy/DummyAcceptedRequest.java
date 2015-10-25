package org.newdeal.core.system.dummy;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.AcceptedRequest;
import org.newdeal.core.system.request.DispatchedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Frederick Vanderbeke
 * @since 24/08/2015.
 */
public class DummyAcceptedRequest extends AbstractComparableRequest implements AcceptedRequest {

    private final DispatchedRequest origin;

    @Override
    public Instant getAcceptDate() {
        return acceptDate;
    }

    private final Instant acceptDate;

    public DummyAcceptedRequest(final DispatchedRequest origin) {
        this.origin = origin;
        acceptDate = origin.getClock().instant();
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public long getTicket() {
        return origin.getTicket();
    }

    @Override
    public Instant getHandleDate() {
        return origin.getHandleDate();
    }

    @Override
    public Instant getDispatchDate() {
        return origin.getDispatchDate();
    }

    @Override
    public Referenceable getProcessorRef() {
        return origin.getProcessorRef();
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
        return Optional.ofNullable(origin);
    }
}
