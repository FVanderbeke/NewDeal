package org.newdeal.core.system.request;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 08/08/2015.
 */
public interface SubmittedRequest extends HandledRequest {
    Instant getSubmissionDate();


}
