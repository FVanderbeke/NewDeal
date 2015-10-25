package org.newdeal.core.dispatch;

import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 30/07/2015
 * @version 0.0.1
 */
public interface ActiveProcessor extends DeclaredProcessor {
    enum State {
        /**
         * Waiting for for request to process.
         */
        IDLE,
        /**
         * Received a request. Checking if it can accept it.
         */
        ACCEPTING,
        /**
         * Request accepted.
         */
        ACCEPTED,
        /**
         * Request has been accepted. Now, trying to initialize communication channels to process it.
         */
        HANDSHAKING,
        /**
         * Communication done.
         */
        HANDSHAKED,
        /**
         * Once communication channels are correct, step before processing.
         */
        PRE_PROCESSING,
        /**
         * Request pre-processed. Ready to process.
         */
        PRE_PROCESSED,
        /**
         * Processing a request.
         */
        PROCESSING,
        /**
         * Processed done.
         */
        PROCESSED,
        /**
         * Post processing a response.
         */
        POST_PROCESSING,
        /**
         * Something went wrong or someone explicitly asked to halt it. Processor is stopped.
         * Must be explicitly resumed with "resume" method.
         */
        HALTED,
        /**
         * Trying to resume a halted processor.
         */
        RESUMING,
        /**
         * Will be deactivated soon.
         */
        DEACTIVATING
    }

    DeclaredProcessor getDeclaredProcessor();

    CompletableFuture<Boolean> halt();

    CompletableFuture<Boolean> resume();

    CompletableFuture<Boolean> deactivate();

}
