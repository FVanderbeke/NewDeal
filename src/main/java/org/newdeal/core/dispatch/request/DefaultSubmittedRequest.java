package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
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
public class DefaultSubmittedRequest extends AbstractComparableRequest implements SubmittedRequest {
    private final Request origin;
    private final Instant handleDate;
    private final Instant submissionDate;
    private final Referenceable stationRef;

    public DefaultSubmittedRequest(Request origin, Referenceable stationRef, Instant handleDate) {
        this.origin = origin;
        this.handleDate = handleDate;
        this.submissionDate = origin.getClock().instant();
        this.stationRef = stationRef;
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public Instant getHandleDate() {
        return handleDate;
    }

    @Override
    public Instant getSubmissionDate() {
        return submissionDate;
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
        return Optional.of(origin);
    }

    @Override
    public Referenceable getStationRef() {
        return stationRef;
    }
}
