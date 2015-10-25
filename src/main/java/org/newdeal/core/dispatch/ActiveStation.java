package org.newdeal.core.dispatch;

import org.newdeal.core.system.annotation.Unsafe;
import org.newdeal.core.system.annotation.Unstable;
import org.newdeal.core.system.request.DispatchedRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * Represents a station that can allocate processors to requests
 *
 * @author Frederick Vanderbeke
 * @since 30/07/2015.
 * @version 0.0.1
 */
public interface ActiveStation extends Station {

    /**
     * The first method to call when the instance is created.
     *
     * @return The currently active and initialized station.
     */
    ActiveStation init();

    /**
     * Called when the instance is deactivated/disposed.
     */
    void destroy();

    /**
     * @return True if the station is active and initialized (so considered as started). False if destroyed/stopped.
     */
    boolean isStarted();

    /**
     * Adding an active processor to the station. This processor will be eligible for request allocation purpose.
     *
     * @param processor Active processor to plug.
     * @param plugDate Date to take into account.
     *
     * @return The plugged instance of this processor.
     */
    PluggedProcessor plug(ActiveProcessor processor, Instant plugDate);

    /**
     * To remove a currently plugged and not yet allocated processor from this station.
     *
     * @param processor Plugged processor to remove.
     *
     * @return True if the processor was plugged and has been successfully removed.
     */
    boolean unplug(PluggedProcessor processor);

    /**
     * <p>Blocking method. Won't leave until a processor has been found for this request.</p>
     * <p>Post-conditions : Can complete exceptionally with :</p>
     * <ul>
     *     <li>BalkedException if the waiting queue size exceeds and no more request can be added to it.</li>
     *     <li>RenegedException when the request is removed from the waiting queue before its allocation.</li>
     * </ul>
     *
     * @param request Request that will be allocated to a processor.
     *
     * @return Instance proving that the submitted request has been dispatched to a processor.
     */
    CompletableFuture<DispatchedRequest> dispatch(SubmittedRequest request);

    /**
     * Would be better to remove and add again the request instead of updating it.
     *
     * @param request Request to update.
     *
     * @return True if it has been updated.
     */
    @Unsafe
    @Unstable
    CompletableFuture<Boolean> update(SubmittedRequest request);

    /**
     * Cancelling the request only if it is still in submission or dispatching steps.
     *
     * @param request Request to abandon.
     *
     * @return True if the request was still waiting for allocation and has been successfully removed from the station.
     */
    CompletableFuture<Boolean> renege(SubmittedRequest request);
}
