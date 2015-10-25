package org.newdeal.core.system;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * @author Frederick Vanderbeke
 * @since 19/06/2015.
 */
public class RelativeClock extends Clock {

    private final Clock clock;

    private final Instant epoch;

    private final long speed;

    public RelativeClock(Instant epoch, long speed, Clock origin) {
        this.epoch = epoch;
        this.speed = speed;
        this.clock = origin;
    }

    public RelativeClock(Instant epoch, long speed) {
        this(epoch, speed, Clock.systemUTC());
    }

    @Override
    public ZoneId getZone() {
        return this.clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        Instant instant = clock.instant();
        Duration gap = Duration.between(epoch, instant);

        return epoch.plus(gap.multipliedBy(speed));
    }
}
