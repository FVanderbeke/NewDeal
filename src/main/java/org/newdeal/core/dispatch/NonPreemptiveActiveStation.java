package org.newdeal.core.dispatch;

import org.newdeal.core.dispatch.exception.BalkedException;
import org.newdeal.core.dispatch.request.WaitingRequest;
import org.newdeal.core.system.Capacity;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Instant;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * -- UNSTABLE --
 *
 * Default implementation for active stations. Used for non preemptive only. Here, a plugged processor is used once.
 * If you want to reuse the processor on this station, you must explicitly plug it again.
 *
 * Uses priority queues to store waiting requests and plugged processors.
 *
 * Priorities are not automatically updated. If priority changes, you must use <code>update</code> methods upon stored
 * elements to force the priority computation
 *
 * @author Frederick Vanderbeke
 * @since 05/08/2015
 * @version 0.0.1
 */
public class NonPreemptiveActiveStation extends AbstractActiveStation {

    /**
     * Sequence used to trace all allocation actions. From the submission to the allocation itself.
     */
    private final AtomicLong tokenSeq = new AtomicLong(0);

    /**
     * Sequence used to generate an "admission" ticket (value used as ticket when entering in the queue; used to
     * identify the entering order).
     */
    private final AtomicLong ticketSeq = new AtomicLong(1);

    /**
     * For allocation process. A queue that is used to trigger the allocation process if a new request is waiting.
     * * This queue is used as arrival requests stream. It will be bounded if the stage model indicates it.
     */
    private final BlockingQueue<WaitingRequest> waitingRequests;

    /**
     * Queue used to block the allocation process when nothing is injected into the waiting processors and requests.
     */
    private final BlockingQueue<Long> allocationTokens = new PriorityBlockingQueue<>();

    /**
     * Worker performing allocation with not allocated stuff.
     */
    private CompletableFuture<Void> allocationWorker;

    /**
     * Specific executor used for the most important long time running task of this class : allocationWorker.
     */
    private final Executor workerExecutor = Executors.newSingleThreadExecutor();

    /**
     * This queue represents the available seats in this waiting room. Each time a request arrives, it takes a
     * ticket from this queue. Basically, it is bound on the defined capacity. If capacity is null ("zero" or
     * equal to 0), the size will be set on the number of currently available/idle processors.
     */
    private final Queue<Long> admissionTickets;

    public NonPreemptiveActiveStation(final Station origin) {
        super(origin);
        if (Capacity.isZero(this.getModel().getCapacity())) {
            throw new IllegalArgumentException("Default active station can't have capacity set to zero.");
        } else if (Capacity.isInfinite(this.getModel().getCapacity())) {
            this.waitingRequests = new PriorityBlockingQueue<>(10, getRequestComparator());
            this.admissionTickets = new ArrayBlockingQueue<>(1);
        } else {
            this.waitingRequests = new PriorityBlockingQueue<>(this.getModel().getCapacity().getValue(), this.getRequestComparator());
            this.admissionTickets = new ArrayBlockingQueue<>(this.getModel().getCapacity().getValue());

            while (ticketSeq.get() <= this.getModel().getCapacity().getValue()) {
                this.admissionTickets.add(ticketSeq.getAndIncrement());
            }
        }
    }

    @Override
    protected void innerStart() {
        this.allocationWorker = CompletableFuture.runAsync(this::performAllocation, this.workerExecutor);
    }

    @Override
    protected void innerStop() {
        this.allocationWorker.cancel(true);
    }
/**
     * The allocation method. Takes not allocated elements from <i>notAllocatedProcessors</i> and <i>notAllocatedRequests</i>
     * and creates the correct responses.
     */
    private void performAllocation() {
        while (this.isStarted()) { // Always running.
            try {
                this.allocationTokens.take();

                PluggedProcessor selectedProcessor = this.idleProcessors.peek(); // Variable loop.
                WaitingRequest selectedRequest = this.waitingRequests.peek();    // Variable loop.

                while (selectedProcessor != null && selectedRequest != null) {
                    selectedRequest.complete(selectedProcessor);

                    this.idleProcessors.remove(selectedProcessor);
                    this.waitingRequests.remove(selectedRequest);

                    selectedProcessor = this.idleProcessors.peek();
                    selectedRequest = this.waitingRequests.peek();
                }
            } catch (Exception e) {
                // Does nothing.
                // TODO : log error.
                //e.printStackTrace();
            }
        }
    }

    @Override
    public PluggedProcessor plug(ActiveProcessor processor, Instant plugDate) {
        PluggedProcessor pluggedProcessor = new DefaultPluggedProcessor(processor, this.getOwner(), this.tokenSeq.incrementAndGet(), plugDate);

        this.idleProcessors.add(pluggedProcessor);

        if (Capacity.isZero(this.getModel().getCapacity())) {
            this.admissionTickets.add(ticketSeq.getAndIncrement());
        }


        this.allocationTokens.add(this.tokenSeq.incrementAndGet());

        return pluggedProcessor;
    }

    @Override
    public boolean unplug(PluggedProcessor processor) {
        boolean removed = this.idleProcessors.remove(processor);

        if (removed && Capacity.isZero(this.getModel().getCapacity())) {
            this.admissionTickets.remove();
        }

        return removed;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public CompletableFuture<DispatchedRequest> dispatch(SubmittedRequest request) {
        CompletableFuture<DispatchedRequest> result = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {

            long ticket = 0;
            try {
                // Checking for free space.
                if (Capacity.isFixed(this.getModel().getCapacity())) {
                    ticket = this.admissionTickets.remove();
                } else if (Capacity.isInfinite(this.getModel().getCapacity())) {
                    ticket = this.ticketSeq.getAndIncrement();
                }
            } catch (Exception e) {
                // Using default queue behavior to check the limit of waiting room size.
                result.completeExceptionally(new BalkedException(request));

                return;
            }

            // Adding the request to the waiting queue.
            this.waitingRequests.add(new WaitingRequest(request, ticket, result));
            this.allocationTokens.add(this.tokenSeq.incrementAndGet());

        });

        return result.whenComplete((dispatched, error) -> {
            // Cleaning internal stuff.
            if (error != null &&
                    this.waitingRequests.remove(request) &&
                    Capacity.isFixed(this.getModel().getCapacity())) {
                this.admissionTickets.add(this.ticketSeq.getAndIncrement());
            }
        });
    }

    private <A, B extends Updatable> CompletableFuture<Boolean> performUpdate(A updatedValue, BlockingQueue<B> queue) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        CompletableFuture
                // First, finding the previous reference in the list.
                .supplyAsync(() -> queue.stream().filter(item -> Objects.equals(item, updatedValue)).findFirst())
                        // Then, updating it.
                .thenApply(optValue -> optValue.map(value -> {
                    // Removing the found reference.
                    if (queue.remove(value)) {
                        //noinspection unchecked
                        queue.add((B)value.copy(updatedValue));
                        this.allocationTokens.add(this.tokenSeq.incrementAndGet());
                        return true;
                    } else {
                        // Remove did not work.
                        return false;
                    }
                }).orElse(false))
                        // Finally, getting the result and sending if the update action succeeded.
                .whenComplete((updated, error) -> {
                    if (error != null) {
                        result.complete(false);
                    } else {
                        result.complete(updated);
                    }
                });

        return result;
    }

    @Override
    public CompletableFuture<Boolean> update(SubmittedRequest request) {
        return this.performUpdate(request, this.waitingRequests);
    }

    @Override
    public CompletableFuture<Boolean> renege(SubmittedRequest request) {
        return CompletableFuture.supplyAsync(() -> this.waitingRequests.stream()
                    .filter(waitingRequest -> Objects.equals(request, waitingRequest))
                    .findFirst()
                    .map(waitingRequest -> {
                        boolean removed = this.waitingRequests.remove(waitingRequest);

                        if (removed) {
                            waitingRequest.renege();
                        }
                        return removed;
                    })
                    .orElse(false));
    }
}
