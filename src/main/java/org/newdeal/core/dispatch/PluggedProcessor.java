package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 01/08/2015
 * @version 0.0.1
 */
public interface PluggedProcessor extends ActiveProcessor, Updatable<ActiveProcessor, PluggedProcessor> {
    Instant getPlugDate();
    long getAllocationToken();
    Referenceable getStationRef();
}
