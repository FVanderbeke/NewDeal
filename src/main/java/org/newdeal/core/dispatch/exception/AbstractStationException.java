package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
abstract class AbstractStationException extends AbstractDispatchException {

    AbstractStationException(Referenceable stationRef) {
        super(stationRef);
    }

    AbstractStationException(Request request, Referenceable stationRef) {
        super(request, stationRef);
    }

    public Referenceable getStationRef() {
        return getOwnerRef();
    }
}
