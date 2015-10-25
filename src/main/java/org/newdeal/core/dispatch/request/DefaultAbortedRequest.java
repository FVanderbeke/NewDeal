package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbortedRequest;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.DispatchedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
public class DefaultAbortedRequest extends AbstractComparableRequest implements AbortedRequest {

    private final DispatchedRequest origin;

    private final Instant abortDate;

    public DefaultAbortedRequest(DispatchedRequest request) {
        this.origin = request;
        this.abortDate = request.getClock().instant();
    }

    @Override
    public Instant getAbortDate() {
        return abortDate;
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
