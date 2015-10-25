package org.newdeal.core.engine;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.*;
import org.newdeal.core.system.*;
import org.newdeal.core.system.request.HandledRequest;
import org.newdeal.core.system.request.SubmittedRequest;

import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * Active dispatch engine manages stations and processors. It handles requests, sends them to a station to dispatch them
 * on a processor.
 *
 * Main entry for requests is the <code>handle</code> method.
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public interface ActiveDispatchEngine extends DeclaredDispatchEngine, ActiveEngine {

    CompletableFuture<Station> declareStation(Model model, Referenceable owner, Comparator<SubmittedRequest> requestComparator, Comparator<PluggedProcessor> processorComparator);

    CompletableFuture<ActiveStation> activateStation(Station station);

    CompletableFuture<DeclaredProcessor> declareProcessor(Processor processor);

    CompletableFuture<ActiveProcessor> activateProcessor(DeclaredProcessor processor);

    CompletableFuture<PluggedProcessor> plug(ActiveProcessor processor, ActiveStation station, Instant plugDate);

    CompletableFuture<ActiveProcessor> unplug(PluggedProcessor processor);

    CompletableFuture<DeclaredProcessor> deactivateProcessor(ActiveProcessor processor);

    CompletableFuture<Processor> undeclareProcessor(DeclaredProcessor processor);

    CompletableFuture<Station> declareFifoStation(Pattern arrivalPattern, Pattern departurePattern, Capacity capacity, NbServers nbServers, Referenceable owner);

    /**
     * Here is the main method. It completely manages the request process workflow.
     * From the default workflow to the error one.
     *
     * @param request The request to handle.
     * @param stationRef The station on which the request must be added.
     *
     * @return A handled request.
     */
    CompletableFuture<HandledRequest> handle(Request request, Referenceable stationRef);

    CompletableFuture<ActiveProcessor> getActiveProcessor(Referenceable processorRef);

    /**
     * Retrieves the active station corresponding to the given reference.
     *
     * @param stationRef Station reference to find.
     *
     * @return Active station version.
     */
    CompletableFuture<ActiveStation> getActiveStation(Referenceable stationRef);

    /**
     * Removing/cancelling the request.
     *
     * Different from the direct cancel from CompletableFuture. Here we can still get the history of the request
     * during the waiting time.
     *
     * @param request Request to abandon.
     *
     * @return True if the request has been abandoned.
     */
    CompletableFuture<Boolean> abandon(Request request);
}
