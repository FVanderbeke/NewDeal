package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 10/10/2015.
 */
public interface DeniedRequest extends AcceptedRequest {
    String getReason();
    Instant getDenialDate();
}
