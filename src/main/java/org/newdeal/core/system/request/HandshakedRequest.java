package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 31/07/2015.
 */
public interface HandshakedRequest extends AcceptedRequest {
    Instant getHandshakeDate();
}
