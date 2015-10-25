package org.newdeal.core.dispatch;

import org.newdeal.core.dispatch.exception.BalkedException;
import org.newdeal.core.dispatch.request.DefaultDispatchedRequest;
import org.newdeal.core.system.Capacity;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * -- UNSTABLE --
 *
 * This kind of station is used to manage non preemptive queues with no waiting room. Request is processed only if a
 * processor is available for allocation. Otherwise, the request is directly balked
 *
 * @author Frederick Vanderbeke
 * @since 14/09/2015
 * @version 0.0.1
 */
public class BasicActiveStation extends AbstractActiveStation {

    /**
     * Ticket to tag the occurred sequence of entering requests.
     */
    private final AtomicLong ticketSeq = new AtomicLong(1);

    public BasicActiveStation(Station origin) {
        super(origin);
        if (!Capacity.isZero(getModel().getCapacity())) {
            throw new IllegalArgumentException("Basic Active Station works only for Zero Capacity Models.");
        }
    }

    @Override
    protected void innerStart() {
    }

    @Override
    protected void innerStop() {
    }

    @Override
    public PluggedProcessor plug(ActiveProcessor processor, Instant plugDate) {
        PluggedProcessor pluggedProcessor = new DefaultPluggedProcessor(processor, this.getOwner(), 0L, plugDate);

        this.idleProcessors.add(pluggedProcessor);

        return pluggedProcessor;
    }

    @Override
    public boolean unplug(PluggedProcessor processor) {
        return this.idleProcessors.remove(processor);
    }

    @Override
    public CompletableFuture<DispatchedRequest> dispatch(SubmittedRequest request) {
        CompletableFuture<DispatchedRequest> result = new CompletableFuture<>();

        CompletableFuture
                .supplyAsync(idleProcessors::remove)
                .whenComplete((selectedProcessor, error) -> {
                    if (error == null) {
                        long ticket = ticketSeq.getAndIncrement();
                        result.complete(new DefaultDispatchedRequest(request, ticket, selectedProcessor));
                    } else if (error instanceof NoSuchElementException) {
                        result.completeExceptionally(new BalkedException(request));
                    } else if (error instanceof CompletionException && error.getCause() instanceof NoSuchElementException) {
                        result.completeExceptionally(new BalkedException(request));
                    } else if (error instanceof CompletionException){
                        result.completeExceptionally(error.getCause());
                    } else {
                        result.completeExceptionally(error);
                    }
                }).whenComplete((processor, error) -> {
                    if (result.isCancelled()) {
                        this.idleProcessors.add(processor);
                    }
                });

        return result;
    }

    @Override
    public CompletableFuture<Boolean> update(SubmittedRequest request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> renege(SubmittedRequest request) {
        return CompletableFuture.completedFuture(false);
    }
}
