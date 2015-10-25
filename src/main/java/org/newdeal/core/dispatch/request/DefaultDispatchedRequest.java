package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.PluggedProcessor;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class DefaultDispatchedRequest extends AbstractComparableRequest implements DispatchedRequest {

    private final SubmittedRequest origin;

    private final long ticket;

    private final Instant dispatchDate;

    private final Referenceable processorRef;

    public DefaultDispatchedRequest(SubmittedRequest origin, long ticket, PluggedProcessor processor) {
        this.origin = origin;
        this.ticket = ticket;
        this.processorRef = processor.getOwner();
        this.dispatchDate = origin.getClock().instant();
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public Instant getHandleDate() {
        return this.origin.getHandleDate();
    }

    @Override
    public Instant getDispatchDate() {
        return this.dispatchDate;
    }

    @Override
    public Referenceable getProcessorRef() {
        return this.processorRef;
    }

    @Override
    public Instant getSubmissionDate() {
        return this.origin.getSubmissionDate();
    }

    @Override
    public Referenceable getStationRef() {
        return this.origin.getStationRef();
    }

    @Override
    public Clock getClock() {
        return this.origin.getClock();
    }

    @Override
    public Instant getCreationDate() {
        return this.origin.getCreationDate();
    }

    @Override
    public Optional<Request> getOrigin() {
        return Optional.of(this.origin);
    }

    @Override
    public long getTicket() {
        return this.ticket;
    }
}
