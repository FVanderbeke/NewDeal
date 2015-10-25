package org.newdeal.core.bean;

import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * A recordable entity with a given declaration date. Tags the instance as persisted (we also record the declaration
 * date). If the instance has been previously activated (used actively by one of the engines), it can give the date
 * of the last deactivation
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public interface Declarable extends Recordable {
    /**
     * @return Declaration date.
     */
    Instant getDeclarationDate();

    /**
     * @return If activated at least once, gives the last deactivation date.
     */
    Optional<Instant> getLastDeactivationDate();
}
