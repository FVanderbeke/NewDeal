package org.newdeal.core.engine;

import org.newdeal.core.bean.Worker;

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
public abstract class AbstractActiveEngine<R extends Engine, D extends DeclaredEngine, A extends ActiveEngine> extends AbstractEngine implements ActiveEngine, Worker<R, D, A> {
    private final D origin;

    private Instant activationDate;
    private Optional<Instant> deactivationDate = Optional.empty();

    private boolean started = false;

    public AbstractActiveEngine(D origin, Instant activationDate) {
        super(origin.getName(), origin.getLabel(), origin.getComment().orElse(null));
        this.origin = origin;
        this.activationDate = activationDate;
        deactivationDate = origin.getLastDeactivationDate();
    }

    @Override
    public Instant getDeclarationDate() {
        return origin.getDeclarationDate();
    }

    @Override
    public Optional<Instant> getLastDeactivationDate() {
        return deactivationDate;
    }

    @Override
    public long getId() {
        return origin.getId();
    }

    @Override
    public String getName() {
        return origin.getName();
    }

    @Override
    public String getLabel() {
        return origin.getLabel();
    }

    @Override
    public Optional<String> getComment() {
        return origin.getComment();
    }

    @Override
    public CompletableFuture<R> getOrigin() {
        return CompletableFuture.completedFuture(getReferenceInstance());
    }

    @Override
    public CompletableFuture<Optional<D>> getDeclaration() {
        return CompletableFuture.completedFuture(Optional.ofNullable(this.origin));
    }

    @Override
    public CompletableFuture<Optional<A>> getActivation() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.started) {
                return Optional.of(getActiveInstance());

            } else {
                return Optional.empty();
            }
        });
    }

    protected abstract A getActiveInstance();

    protected abstract R getReferenceInstance();

    @Override
    public CompletableFuture<D> init() {
        return CompletableFuture.completedFuture(origin);
    }

    @Override
    public CompletableFuture<A> start(Instant activationDate) {
        return init().thenCompose(dpe -> CompletableFuture.supplyAsync(() -> {
            if (!started) {
                this.activationDate = activationDate;
                this.started = true;
            }

            return getActiveInstance();
        }));
    }

    @Override
    public CompletableFuture<D> stop(Instant deactivationDate) {
        return CompletableFuture.supplyAsync(() -> {
            if (started) {
                this.deactivationDate = Optional.ofNullable(deactivationDate);
                this.started = false;
            }

            return this.origin;
        });
    }

    @Override
    public CompletableFuture<R> destroy() {
        return stop(org.newdeal.core.system.System.systemClock().instant()).thenApplyAsync(dpe -> {
            return getReferenceInstance();
        });
    }

    @Override
    public Instant getLastActivationDate() {
        return this.activationDate;
    }
}
