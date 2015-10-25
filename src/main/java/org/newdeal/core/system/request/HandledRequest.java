package org.newdeal.core.system.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 11/09/2015.
 */
public interface HandledRequest extends Request {
    Instant getHandleDate();

    Referenceable getStationRef();

}
