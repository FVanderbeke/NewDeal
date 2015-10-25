package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 14/09/2015.
 */
public interface BalkedRequest extends HandledRequest {
    Instant getBalkDate();
}
