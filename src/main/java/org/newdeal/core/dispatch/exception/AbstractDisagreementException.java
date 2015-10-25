package org.newdeal.core.dispatch.exception;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * Kind of exception used in the request process flow management to loop on the accept or handshake step
 *
 * @author Frederick Vanderbeke
 * @since 10/10/2015.
 * @version 0.0.1
 */
abstract class AbstractDisagreementException extends Exception {
    private final String reason;

    private final Instant occurrenceDate;

    AbstractDisagreementException(String reason, Instant occurrenceDate) {
        this.reason = reason;
        this.occurrenceDate = occurrenceDate;
    }

    public String getReason() {
        return reason;
    }

    public Instant getOccurrenceDate() {
        return occurrenceDate;
    }
}
