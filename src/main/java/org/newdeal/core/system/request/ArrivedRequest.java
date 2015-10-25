package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 05/08/2015.
 */
public interface ArrivedRequest extends DispatchedRequest {
    Instant getArrivalDate();
}
