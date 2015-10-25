package org.newdeal.core.system.dummy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Frederick Vanderbeke
 * @since 08/08/2015.
 *
 * Clock used to give list
 */
public class DummyArrayClock extends Clock {

    private final List<Instant> origin;
    private final Queue<Instant> instants;

    public DummyArrayClock(final List<Instant> instants) {
        this.origin = new ArrayList<>(instants);
        this.instants = new LinkedBlockingQueue<>(instants);
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new DummyArrayClock(origin);
    }

    @Override
    public Instant instant() {
        return instants.poll();
    }
}
