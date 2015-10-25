package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;

/**
 * -- UNSTABLE --
 *
 * Thrown when trying to use an inactive (but declared) station.
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class InactiveStationException extends AbstractStationException {
    public InactiveStationException(Referenceable stationRef) {
        super(stationRef);
    }

    public InactiveStationException(Request request, Referenceable stationRef) {
        super(request, stationRef);
    }
}
