package org.newdeal.core.system;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * @author Frederick Vanderbeke
 * @since 19/06/2015.
 */
public class AbsoluteClock extends Clock {
    private final Clock clock = Clock.systemUTC();

    private static AbsoluteClock ourInstance = new AbsoluteClock();

    public static AbsoluteClock getInstance() {
        return ourInstance;
    }

    private AbsoluteClock() {
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }
}
