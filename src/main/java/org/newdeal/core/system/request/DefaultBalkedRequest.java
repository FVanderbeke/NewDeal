package org.newdeal.core.system.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Frederick Vanderbeke
 * @since 15/09/2015.
 */
public class DefaultBalkedRequest extends AbstractComparableRequest implements BalkedRequest {

    private final SubmittedRequest origin;

    private final Instant balkDate;

    public DefaultBalkedRequest(SubmittedRequest origin) {
        this.origin = origin;
        this.balkDate = origin.getClock().instant();
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public Instant getBalkDate() {
        return this.balkDate;
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
