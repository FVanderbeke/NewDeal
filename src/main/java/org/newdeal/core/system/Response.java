package org.newdeal.core.system;

import org.newdeal.core.system.request.PreProcessedRequest;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 28/05/2015.
 */
public interface Response extends PreProcessedRequest {
    Instant getResponseDate();
}
