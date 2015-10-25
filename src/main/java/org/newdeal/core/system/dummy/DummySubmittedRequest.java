package org.newdeal.core.system.dummy;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Frederick Vanderbeke
 * @since 24/09/2015.
 */
public class DummySubmittedRequest extends AbstractComparableRequest implements SubmittedRequest {
    private final Request origin;
    private final Instant submissionDate;
    private final Referenceable stationRef;
    private final Instant handleDate;

    public DummySubmittedRequest(Request origin, Instant submissionDate, Referenceable stationRef, Instant handleDate) {
        this.origin = origin;
        this.submissionDate = submissionDate;
        this.stationRef = stationRef;
        this.handleDate = handleDate;
    }

    @Override
    public Instant getSubmissionDate() {
        return submissionDate;
    }

    @Override
    public Instant getHandleDate() {
        return handleDate;
    }

    @Override
    public Referenceable getStationRef() {
        return stationRef;
    }

    @Override
    public long getId() {
        return origin.getId();
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
