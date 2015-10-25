package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 17/10/2015
 */
public interface CancelledRequest extends HandledRequest {
    Instant getCancelDate();
}
