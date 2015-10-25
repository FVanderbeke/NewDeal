package org.newdeal.core.bean;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * Indicates that the current instance is active (currently used by one of the engines)
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015.
 * @version 0.0.1
 */
public interface Activable extends Declarable {
    /**
     * @return Activation date.
     */
    Instant getLastActivationDate();
}
