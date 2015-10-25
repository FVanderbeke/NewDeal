package org.newdeal.core.bean;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public interface Worker<R extends Referenceable, D extends Declarable, A extends Activable> {
    CompletableFuture<R> getOrigin();
    CompletableFuture<Optional<D>> getDeclaration();
    CompletableFuture<Optional<A>> getActivation();

    CompletableFuture<D> init();

    CompletableFuture<A> start(Instant activationDate);
    CompletableFuture<D> stop(Instant deactivationDate);

    CompletableFuture<R> destroy();
}
