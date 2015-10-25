package org.newdeal.core.system.dummy;

import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Frederick Vanderbeke
 * @since 08/08/2015.
 */
public class DummyRequest extends AbstractComparableRequest {

    private final static AtomicLong ID_SEQ = new AtomicLong(1);

    private final long id = ID_SEQ.getAndIncrement();

    private final Clock clock;

    private final Instant creationDate;

    public DummyRequest(Clock clock) {
        this.clock = clock;
        creationDate = clock.instant();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    @Override
    public Instant getCreationDate() {
        return creationDate;
    }

    @Override
    public Optional<Request> getOrigin() {
        return Optional.empty();
    }
}
