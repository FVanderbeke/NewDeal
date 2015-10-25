package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;

/**
 * -- UNSTABLE --
 *
 * Thrown when trying to use a not declared station.
 *
 * @author Frederick Vanderbeke
 * @since 08/08/2015
 * @version 0.0.1
 */
public class UnknownStationException extends AbstractStationException {

    public UnknownStationException(Referenceable stationRef) {
        super(stationRef);
    }

}
