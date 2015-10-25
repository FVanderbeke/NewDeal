package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 09/10/2015.
 */
public interface RefusedRequest extends DispatchedRequest {
    String getReason();

    Instant getRefusalDate();
}
