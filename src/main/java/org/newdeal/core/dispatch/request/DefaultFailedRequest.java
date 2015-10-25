package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.FailedRequest;
import org.newdeal.core.system.request.HandledRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 10/10/2015
 * @version 0.0.1
 */
public class DefaultFailedRequest extends AbstractComparableRequest implements FailedRequest {

    private final HandledRequest origin;

    private final Instant failureDate;

    private final Throwable reason;

    public DefaultFailedRequest(HandledRequest origin, Throwable reason) {
        this(origin, origin.getClock().instant(), reason);
    }

    private DefaultFailedRequest(HandledRequest origin, Instant failureDate, Throwable reason) {
        this.origin = origin;
        this.failureDate = failureDate;
        this.reason = reason;
    }

    @Override
    public Instant getFailureDate() {
        return failureDate;
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
        return Optional.ofNullable(origin);
    }

    @Override
    public Throwable getReason() {
        return reason;
    }
}
