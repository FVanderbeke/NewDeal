package org.newdeal.core.system.dummy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * @author Frederick Vanderbeke
 * @since 27/08/2015.
 */
public class DummyIncrClock extends Clock {
    private Instant start;
    private final Duration incrStep;

    public DummyIncrClock(Instant start, Duration incrStep) {
        this.start = start;
        this.incrStep = incrStep;
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new DummyIncrClock(start, incrStep);
    }

    @Override
    public synchronized Instant instant() {
        Instant result = start;
        start = start.plus(incrStep);
        return result;
    }
}
