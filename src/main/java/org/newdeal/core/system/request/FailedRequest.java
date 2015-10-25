package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 10/10/2015.
 */
public interface FailedRequest extends HandledRequest {
    Instant getFailureDate();

    Throwable getReason();

}
