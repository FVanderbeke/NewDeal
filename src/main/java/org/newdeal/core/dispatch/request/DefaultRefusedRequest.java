package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.RefusedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 09/10/2015
 * @version 0.0.1
 */
public class DefaultRefusedRequest extends AbstractComparableRequest implements RefusedRequest {

    private final DispatchedRequest origin;

    private final String reason;

    private final Instant refusalDate;

    public DefaultRefusedRequest(DispatchedRequest origin, String reason, Instant refusalDate) {
        this.origin = origin;
        this.reason = reason;
        this.refusalDate = refusalDate;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public Instant getRefusalDate() {
        return this.refusalDate;
    }

    @Override
    public long getTicket() {
        return this.origin.getTicket();
    }

    @Override
    public Instant getDispatchDate() {
        return this.origin.getDispatchDate();
    }

    @Override
    public Referenceable getProcessorRef() {
        return this.origin.getProcessorRef();
    }

    @Override
    public Instant getSubmissionDate() {
        return this.origin.getSubmissionDate();
    }

    @Override
    public Instant getHandleDate() {
        return this.origin.getHandleDate();
    }

    @Override
    public Referenceable getStationRef() {
        return this.origin.getStationRef();
    }

    @Override
    public long getId() {
        return this.origin.getId();
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
        return Optional.ofNullable(this.origin);
    }
}
