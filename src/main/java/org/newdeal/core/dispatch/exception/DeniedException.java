package org.newdeal.core.dispatch.exception;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * Caused when handshake step of request processing failed
 *
 * @author Frederick Vanderbeke
 * @since 10/10/2015
 * @version 0.0.1
 */
public class DeniedException extends AbstractDisagreementException {

    public DeniedException(String reason, Instant occurrenceDate) {
        super(reason, occurrenceDate);
    }
}
