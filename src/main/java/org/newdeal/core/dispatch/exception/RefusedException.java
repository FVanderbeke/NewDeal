package org.newdeal.core.dispatch.exception;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * Thrown when the request is not accepted by the selected processor
 *
 * @author Frederick Vanderbeke
 * @since 09/10/2015
 * @version 0.0.1
 */
public class RefusedException extends AbstractDisagreementException {

    public RefusedException(String reason, Instant occurrenceDate) {
        super(reason, occurrenceDate);
    }
}
