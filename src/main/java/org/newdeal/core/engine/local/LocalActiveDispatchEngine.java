package org.newdeal.core.engine.local;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.*;
import org.newdeal.core.dispatch.exception.*;
import org.newdeal.core.dispatch.request.DefaultAbortedRequest;
import org.newdeal.core.dispatch.request.DefaultCancelledRequest;
import org.newdeal.core.dispatch.request.DefaultFailedRequest;
import org.newdeal.core.dispatch.request.DefaultRenegedRequest;
import org.newdeal.core.engine.AbstractActiveEngine;
import org.newdeal.core.engine.ActiveDispatchEngine;
import org.newdeal.core.engine.DeclaredDispatchEngine;
import org.newdeal.core.engine.DispatchEngine;
import org.newdeal.core.system.*;
import org.newdeal.core.system.request.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public class LocalActiveDispatchEngine extends AbstractActiveEngine<DispatchEngine, DeclaredDispatchEngine, ActiveDispatchEngine> implements ActiveDispatchEngine {

    private final AtomicLong pidSeq = new AtomicLong(1);

    private final ConcurrentMap<Referenceable, Station> stations = new ConcurrentHashMap<>(10, .8f);

    private final ConcurrentMap<Referenceable, DeclaredProcessor> processors = new ConcurrentHashMap<>(100, .8f);

    private final ConcurrentMap<Long, HandledProcess> handledProcesses = new ConcurrentHashMap<>(100, .8f);

    public LocalActiveDispatchEngine(DeclaredDispatchEngine declaredDispatchEngine, Instant activationDate) {
        super(declaredDispatchEngine, activationDate);
    }

    @Override
    protected ActiveDispatchEngine getActiveInstance() {
        return this;
    }

    @Override
    protected DispatchEngine getReferenceInstance() {
        return new LocalDispatchEngine(this.getName(), this.getLabel(), this.getComment().orElse(null));
    }

    @Override
    public CompletableFuture<Station> declareStation(Model model, Referenceable owner, Comparator<SubmittedRequest> requestComparator, Comparator<PluggedProcessor> processorComparator) {
        return CompletableFuture.supplyAsync(() -> {
            stations.putIfAbsent(owner, new DefaultStation(model, owner));
            return stations.get(owner);
        });
    }

    private <R> CompletableFuture<R> performFuture(Consumer<CompletableFuture<R>> action) {
        CompletableFuture<R> result = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                action.accept(result);
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });

        return result;

    }

    @Override
    public CompletableFuture<DeclaredProcessor> declareProcessor(Processor processor) {
        return performFuture(result -> {
            processors.putIfAbsent(processor.getOwner(), new DefaultDeclaredProcessor(pidSeq.getAndIncrement(), processor));
            DeclaredProcessor dc = processors.get(processor.getOwner());

            if (dc == null) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            }
            result.complete(dc);
        });
    }

    @Override
    public CompletableFuture<ActiveProcessor> activateProcessor(DeclaredProcessor processor) {
        return this.<ActiveProcessor>performFuture(result -> {
            DeclaredProcessor tmpProc = processors.get(processor.getOwner());

            if (tmpProc != null && !(tmpProc instanceof ActiveProcessor)) {
                processors.replace(processor.getOwner(), processor, new DefaultActiveProcessor(processor));
                tmpProc = processors.get(processor.getOwner());
            }

            if (tmpProc == null) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            } else if (tmpProc instanceof ActiveProcessor) {
                result.complete((ActiveProcessor) tmpProc);
            } else {
                result.completeExceptionally(new InactiveProcessorException(processor.getOwner()));
            }
        });
    }

    @Override
    public CompletableFuture<PluggedProcessor> plug(ActiveProcessor processor, ActiveStation station, Instant plugDate) {
        return this.<PluggedProcessor>performFuture(result -> {
            Station askedStation = stations.get(station.getOwner());
            if (askedStation == null) {
                result.completeExceptionally(new UnknownStationException(station.getOwner()));
            } else if (!(askedStation instanceof ActiveStation)) {
                result.completeExceptionally(new InactiveStationException(station.getOwner()));
            } else if (processors.containsKey(processor.getOwner())) {
                PluggedProcessor pluggedProcessor = ((ActiveStation) askedStation).plug(processor, plugDate);
                result.complete(pluggedProcessor);
            } else {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            }
        });
    }

    @Override
    public CompletableFuture<ActiveProcessor> unplug(PluggedProcessor processor) {
        return this.<ActiveProcessor>performFuture(result -> {
            // Getting station.
            Station askedStation = stations.get(processor.getStationRef());
            if (askedStation == null) {
                result.completeExceptionally(new UnknownStationException(processor.getStationRef()));
                return;
            } else if (!(askedStation instanceof ActiveStation)) {
                result.completeExceptionally(new InactiveStationException(processor.getStationRef()));
                return;
            } else if (!processors.containsKey(processor.getOwner())) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
                return;
            }
            // Unplugging the processor.
            ((ActiveStation) askedStation).unplug(processor);

            Processor foundProcessor = processors.get(processor.getOwner());

            if (foundProcessor == null) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            } else if (foundProcessor instanceof ActiveProcessor) {
                result.complete((ActiveProcessor) foundProcessor);
            } else {
                result.completeExceptionally(new InactiveProcessorException(processor.getOwner()));
            }
        });
    }

    @Override
    public CompletableFuture<DeclaredProcessor> deactivateProcessor(ActiveProcessor processor) {
        return this.<DeclaredProcessor>performFuture(result -> {
            DeclaredProcessor tmpProc = processors.get(processor.getOwner());

            if (tmpProc != null && tmpProc instanceof ActiveProcessor) {
                processors.replace(processor.getOwner(), processor, ((ActiveProcessor) tmpProc).getDeclaredProcessor());
                tmpProc = processors.get(processor.getOwner());
            }

            if (tmpProc == null) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            } else {
                result.complete(tmpProc);
            }
        });
    }

    @Override
    public CompletableFuture<Processor> undeclareProcessor(DeclaredProcessor processor) {
        return performFuture(result -> {
            DeclaredProcessor dc = processors.remove(processor.getOwner());

            if (dc == null) {
                result.completeExceptionally(new UnknownProcessorException(processor.getOwner()));
            } else {
                result.complete(dc.getInitialProcessor());
            }
        });
    }

    @Override
    public CompletableFuture<Station> declareFifoStation(Pattern arrivalPattern, Pattern departurePattern, Capacity capacity, NbServers nbServers, Referenceable owner) {
        return declareStation(new Model(arrivalPattern, departurePattern, Discipline.FIFO, capacity, nbServers), owner, Station.DEFAULT_REQUEST_COMPARATOR, Station.DEFAULT_PROCESSOR_COMPARATOR);
    }

    @Override
    public CompletableFuture<HandledRequest> handle(Request request, Referenceable stationRef) {
        HandledProcess defaultProcess = new HandledProcess(this, request, stationRef);
        this.handledProcesses.putIfAbsent(request.getId(), defaultProcess);

        final HandledProcess storedProcess  = this.handledProcesses.get(request.getId());

        if (storedProcess == null) {
            return CompletableFuture.completedFuture(new DefaultBalkedRequest(defaultProcess.createSubmittedRequest()));
        }

        CompletableFuture<HandledRequest> result = storedProcess.submit()
                .thenComposeAsync(storedProcess::dispatch)
                .thenComposeAsync(storedProcess::accept)
                .thenComposeAsync(storedProcess::handshake)
                .thenComposeAsync(storedProcess::preProcess)
                .thenComposeAsync(storedProcess::process)
                .thenComposeAsync(storedProcess::postProcess)
                .thenComposeAsync(storedProcess::terminate)
                .thenApply(terminatedResponse -> (HandledRequest)terminatedResponse)
                .exceptionally(error -> {

                    //
                    // Extra management for possible errors.
                    //
                    if (error instanceof BalkedException) {
                        SubmittedRequest submittedRequest = ((BalkedException) error).getRequest();
                        return new DefaultBalkedRequest(submittedRequest);
                    } else if (error.getCause() instanceof BalkedException) {
                        SubmittedRequest submittedRequest = ((BalkedException) error.getCause()).getRequest();
                        return new DefaultBalkedRequest(submittedRequest);
                    } else if (error instanceof RenegedException) {
                        SubmittedRequest submittedRequest = ((RenegedException) error).getRequest();
                        return new DefaultRenegedRequest(submittedRequest);
                    } else if (error.getCause() instanceof RenegedException) {
                        SubmittedRequest submittedRequest = ((RenegedException) error.getCause()).getRequest();
                        return new DefaultRenegedRequest(submittedRequest);
                    } else if (error instanceof AbortedException) {
                        DispatchedRequest dispatchedRequest = ((AbortedException) error).getRequest();
                        return new DefaultAbortedRequest(dispatchedRequest);
                    } else if (error.getCause() instanceof AbortedException) {
                        DispatchedRequest dispatchedRequest = ((AbortedException) error.getCause()).getRequest();
                        return new DefaultAbortedRequest(dispatchedRequest);
                    } else if (error instanceof CancelledException) {
                        AcceptedRequest acceptedRequest = ((CancelledException) error).getRequest();
                        return new DefaultCancelledRequest(acceptedRequest);
                    } else if (error.getCause() instanceof CancelledException) {
                        AcceptedRequest acceptedRequest = ((CancelledException) error.getCause()).getRequest();
                        return new DefaultCancelledRequest(acceptedRequest);
                    } else if (error instanceof CompletionException) {
                        // Unboxing the original error.
                        return new DefaultFailedRequest(storedProcess.createSubmittedRequest(), error.getCause());
                    } else {
                        return new DefaultFailedRequest(storedProcess.createSubmittedRequest(), error);
                    }
                });

        result.whenComplete((__, error) -> {
            if (error instanceof CancellationException) {
                //
                // Forcing the result. No history on this resulting request.
                //
                result.obtrudeValue(new DefaultRenegedRequest(defaultProcess.createSubmittedRequest()));

                //
                // Cleaning the station if needed (to avoid locked queries).
                //
                getActiveStation(stationRef).thenAccept(station -> station.renege(defaultProcess.createSubmittedRequest()));
            }

            //
            // Cleaning internal dispatch processes cache.
            //
            Optional.ofNullable(this.handledProcesses.remove(request.getId())).ifPresent(HandledProcess::kill);
        });
        return result;
    }


    private ActiveStation newActiveStation(Station station) {
        if (Capacity.isZero(station.getModel().getCapacity())) {
            return new BasicActiveStation(station);
        } else {
            return new NonPreemptiveActiveStation(station);
        }
    }

    @Override
    public CompletableFuture<ActiveStation> activateStation(Station station) {
        return this.<ActiveStation>performFuture(result -> {

            Station tmpStation = stations.get(station.getOwner());

            if (tmpStation != null && !(tmpStation instanceof ActiveStation)) {
                stations.replace(station.getOwner(), station, newActiveStation(station));
                tmpStation = stations.get(station.getOwner());
            }

            if (tmpStation == null) {
                result.completeExceptionally(new UnknownStationException(station.getOwner()));
            } else if (tmpStation instanceof ActiveStation) {
                result.complete((ActiveStation) tmpStation);
            } else {
                result.completeExceptionally(new InactiveStationException(station.getOwner()));
            }

        }).thenApply(ActiveStation::init);
    }

    @Override
    public CompletableFuture<ActiveStation> getActiveStation(Referenceable stationRef) {
        return this.performFuture(result -> {
            Station station = stations.get(stationRef);
            if (station == null) {
                result.completeExceptionally(new UnknownStationException(stationRef));
            } else if (!(station instanceof ActiveStation)) {
                result.completeExceptionally(new InactiveStationException(stationRef));
            } else {
                result.complete((ActiveStation) station);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> abandon(Request request) {
        return CompletableFuture.supplyAsync(() -> this.handledProcesses.get(request.getId())).thenCompose(process -> process.interrupt(request)).exceptionally(error -> {
            error.printStackTrace();
            return false;
        });
    }


    @Override
    public CompletableFuture<ActiveProcessor> getActiveProcessor(Referenceable processorRef) {
        return this.performFuture(result -> {
            Processor processor = processors.get(processorRef);
            if (processor == null) {
                result.completeExceptionally(new UnknownProcessorException(processorRef));
            } else if (!(processor instanceof ActiveProcessor)) {
                result.completeExceptionally(new InactiveProcessorException(processorRef));
            } else {
                result.complete((ActiveProcessor) processor);
            }
        });
    }
}
