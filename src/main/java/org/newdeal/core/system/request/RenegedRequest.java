package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 16/09/2015.
 */
public interface RenegedRequest extends HandledRequest {
    Instant getRenegeDate();
}
