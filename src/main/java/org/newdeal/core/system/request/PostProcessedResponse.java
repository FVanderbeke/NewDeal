package org.newdeal.core.system.request;

import org.newdeal.core.system.Response;

import java.time.Instant;

/**
 * @author Frederick Vanderbeke
 * @since 07/06/2015.
 */
public interface PostProcessedResponse extends Response {
    Instant getPostProcessDate();
}
