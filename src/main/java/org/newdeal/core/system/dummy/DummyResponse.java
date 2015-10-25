package org.newdeal.core.system.dummy;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.PreProcessedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Frederick Vanderbeke
 * @since 25/08/2015.
 */
public class DummyResponse extends AbstractComparableRequest implements Response {
    private final PreProcessedRequest origin;

    private final Instant responseDate;

    public DummyResponse(PreProcessedRequest request) {
        this.origin = request;
        this.responseDate = request.getClock().instant();
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public Instant getResponseDate() {
        return responseDate;
    }

    @Override
    public Instant getPreProcessDate() {
        return origin.getPreProcessDate();
    }

    @Override
    public Instant getAcceptDate() {
        return origin.getAcceptDate();
    }

    @Override
    public Instant getHandshakeDate() {
        return origin.getHandshakeDate();
    }

    @Override
    public long getTicket() {
        return origin.getTicket();
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
    public Instant getHandleDate() {
        return origin.getHandleDate();
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
