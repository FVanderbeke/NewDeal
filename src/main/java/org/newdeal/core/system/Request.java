package org.newdeal.core.system;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Frederick Vanderbeke
 * @since 27/05/2015.
 */
public interface Request {
    long getId();

    Clock getClock();

    Instant getCreationDate();

    Optional<Request> getOrigin();
}
