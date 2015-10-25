package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.RenegedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 24/09/2015
 * @version 0.0.1
 */
public class DefaultRenegedRequest extends AbstractComparableRequest implements RenegedRequest {

    private final SubmittedRequest origin;

    private final Instant renegeDate;

    public DefaultRenegedRequest(SubmittedRequest request) {
        this.origin = request;
        this.renegeDate = request.getClock().instant();
    }

    @Override
    public Instant getRenegeDate() {
        return this.renegeDate;
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
