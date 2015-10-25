package org.newdeal.core.engine;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 18/06/2015
 * @version 0.0.1
 */
public interface SystemEngine {
    void init();

    void destroy();

    CompletableFuture<DeclaredDispatchEngine> declareDispatchEngine(DispatchEngine engine, Instant declarationDate);

    CompletableFuture<ActiveDispatchEngine> activateDispatchEngine(DeclaredDispatchEngine engine, Instant activationDate);

}
