package org.newdeal.core.system;

import java.time.Clock;

/**
 * @author Frederick Vanderbeke
 * @since 22/06/2015.
 */
public interface System {

    static Clock systemClock() {
        return Clock.systemUTC();
    }

}
