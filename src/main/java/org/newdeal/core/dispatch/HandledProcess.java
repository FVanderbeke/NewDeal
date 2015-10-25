package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.exception.DeniedException;
import org.newdeal.core.dispatch.exception.RefusedException;
import org.newdeal.core.dispatch.exception.RenegedException;
import org.newdeal.core.dispatch.request.DefaultDeniedRequest;
import org.newdeal.core.dispatch.request.DefaultRefusedRequest;
import org.newdeal.core.dispatch.request.DefaultSubmittedRequest;
import org.newdeal.core.engine.ActiveDispatchEngine;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

import java.time.Instant;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * -- UNSTABLE --
 *
 * Entity responsible for internal dispatching process of a given request
 *
 * @author Frederick Vanderbeke
 * @since 09/10/2015
 * @version 0.0.1
 */
public class HandledProcess {

    private final ActiveDispatchEngine engine;

    private final Request arrivedRequest;

    private final Referenceable stationRef;

    private final Instant handleDate;

    private final AtomicReference<HandledProcessStep> currentStep = new AtomicReference<>();

    public HandledProcess(ActiveDispatchEngine engine, Request arrivedRequest, Referenceable stationRef) {
        this.engine = engine;

        this.arrivedRequest = arrivedRequest;

        this.stationRef = stationRef;

        this.handleDate = arrivedRequest.getClock().instant();

        final SubmittedRequest submittedRequest = this.createSubmittedRequest();

        CompletableFuture<? extends Request> submittedTask = this.engine
                .getActiveStation(this.stationRef)
                .thenApply(station -> submittedRequest);

        this.currentStep.set(new HandledProcessStep(HandledProcessState.SUBMITTED, submittedRequest, submittedTask));
    }

    public SubmittedRequest createSubmittedRequest() {
        return new DefaultSubmittedRequest(this.arrivedRequest, stationRef, this.handleDate);
    }

    public CompletableFuture<SubmittedRequest> submit() {
        CompletableFuture<SubmittedRequest> result = new CompletableFuture<>();

        this.currentStep.get().getTask().whenComplete((request, error) -> {
            if (error == null) {
                // Correct ending.
                result.complete((SubmittedRequest) request);
            } else if (error instanceof CancellationException) {
                result.completeExceptionally(new RenegedException(this.createSubmittedRequest()));
            } else if (error instanceof CompletionException) {
                result.completeExceptionally(error.getCause());
            } else {
                result.completeExceptionally(error);
            }
        });

        return result;
    }

    public CompletableFuture<DispatchedRequest> dispatch(SubmittedRequest request) {
        CompletableFuture<DispatchedRequest> result = new CompletableFuture<>();

        this.currentStep.set(new HandledProcessStep(HandledProcessState.DISPATCHING, request, result));

        CompletableFuture<DispatchedRequest> performing = this.engine.getActiveStation(request.getStationRef())
                .thenCompose(station -> station.dispatch(request))
                .whenComplete((dispatched, error) -> {
                    if (result.isDone()) {
                        //noinspection UnnecessaryReturnStatement
                        return;
                    } else if (error == null) {
                        result.complete(dispatched);
                    } else if (error instanceof CancellationException) {
                        result.completeExceptionally(new RenegedException(request));
                    } else if (error instanceof CompletionException) {
                        result.completeExceptionally(error.getCause());
                    } else {
                        result.completeExceptionally(error);
                    }
                });

        //
        // If request is cancelled, also cancelling current performing task.
        //
        result.whenComplete((__, error) -> {
            if (error instanceof CancellationException && !performing.isDone()) {
                performing.cancel(true);
            }
        });

        return result;
    }

    private <R> CompletableFuture<R> toFuture(Either<Exception, R> either) {
        CompletableFuture<R> result = new CompletableFuture<>();

        either.fold(result::completeExceptionally, result::complete);

        return result;
    }

    private <A extends DispatchedRequest, B extends DispatchedRequest> CompletableFuture<B> reDoIfDisagreed(
            HandledProcessState newState,
            A request,
            Function<ActiveProcessor, Either<Exception, B>> action,
            Function<Throwable, Boolean> disagreed,
            Consumer<Throwable> onDisagreement,
            CompletableFuture<B> result) {

        this.currentStep.set(new HandledProcessStep(newState, request, result));

        //
        // While the request is rejected, will perform a "recursive" agreeing operation.
        //
        CompletableFuture<B> performing = this.engine.getActiveProcessor(request.getProcessorRef())
                .thenComposeAsync(processor -> this.toFuture(action.apply(processor)))
                .whenComplete((accepted, error) -> {
                    if (error == null) {
                        // Normal ending.
                        result.complete(accepted);
                    } else if (error instanceof CancellationException) {
                        // Performing a "Bidirectional" cancel.
                        result.cancel(true);
                    } else if (disagreed.apply(error)) {
                        // Performing the recursive call because the previous one has been rejected.
                        onDisagreement.accept(error);
                    } else if (disagreed.apply(error.getCause())) {
                        // Performing the recursive call because the previous one has been rejected.
                        onDisagreement.accept(error.getCause());
                    } else if (error instanceof CompletionException) {
                        // An other type of error occurred. Unboxing the original error and sending it.
                        result.completeExceptionally(error.getCause());
                    } else {
                        // An other type of error occurred.
                        result.completeExceptionally(error);
                    }
                });


        //
        // At the really end, if the performing task is still running, cancelling it.
        //
        return result.whenComplete((__, error) -> {
            if (error != null && !performing.isDone()) {
                performing.cancel(true);
            }
        });
    }

    private CompletableFuture<AcceptedRequest> accept(DispatchedRequest request, CompletableFuture<AcceptedRequest> result) {
        return this.reDoIfDisagreed(
                HandledProcessState.ACCEPTING,
                request,
                processor -> processor.accept(request),
                error -> error instanceof RefusedException,
                error -> this.onRefusal(request, (RefusedException) error, result),
                result);
    }

    private void onRefusal(DispatchedRequest request, RefusedException exception, CompletableFuture<AcceptedRequest> result) {
        this.dispatch(new DefaultRefusedRequest(request, exception.getReason(), exception.getOccurrenceDate()))
                .thenCompose(dispatched -> this.accept(dispatched, result));
    }

    public CompletableFuture<AcceptedRequest> accept(DispatchedRequest request) {
        return this.accept(request, new CompletableFuture<>());
    }

    private void onDenial(AcceptedRequest request, DeniedException exception, CompletableFuture<HandshakedRequest> result) {
        this.dispatch(new DefaultDeniedRequest(request, exception.getReason(), exception.getOccurrenceDate()))
                .thenCompose(this::accept)
                .thenCompose(accepted -> this.handshake(accepted, result));
    }

    private CompletableFuture<HandshakedRequest> handshake(AcceptedRequest request, CompletableFuture<HandshakedRequest> result) {
        return this.reDoIfDisagreed(
                HandledProcessState.HANDSHAKING,
                request,
                processor -> processor.handshake(request),
                error -> error instanceof DeniedException,
                error -> this.onDenial(request, (DeniedException) error, result),
                result);
    }

    public CompletableFuture<HandshakedRequest> handshake(AcceptedRequest request) {
        return this.handshake(request, new CompletableFuture<>());
    }

    private <A extends DispatchedRequest, B extends Request> CompletableFuture<B> performOnRequest(HandledProcessState newState, A request, Function<Processor, Either<Exception, B>> action) {

        CompletableFuture<B> result = new CompletableFuture<>();

        this.currentStep.set(new HandledProcessStep(newState, request, result));


        CompletableFuture<B> performing = this.engine.getActiveProcessor(request.getProcessorRef())
                .thenApplyAsync(action)
                .thenComposeAsync(this::toFuture)
                .whenComplete((value, error) -> {
                    if (error instanceof CancellationException) {
                        // Performing a "Bidirectional" cancel.
                        result.cancel(true);
                    } else if (error == null) {
                        result.complete(value);
                    } else if (error instanceof CompletionException) {
                        // An other type of error occurred. Unboxing the original error and sending it.
                        result.completeExceptionally(error.getCause());
                    } else {
                        result.completeExceptionally(error);
                    }
                });

        result.whenComplete((__, error) -> {
            if (error instanceof CancellationException && !performing.isDone()) {
                performing.cancel(true);
            }
        });


        return result;
    }

    public CompletableFuture<PreProcessedRequest> preProcess(HandshakedRequest request) {
        return this.performOnRequest(HandledProcessState.PRE_PROCESSING, request, processor -> processor.preProcess(request));
    }

    public CompletableFuture<Response> process(PreProcessedRequest request) {
        return this.performOnRequest(HandledProcessState.PROCESSING, request, processor -> processor.process(request));
    }

    public CompletableFuture<PostProcessedResponse> postProcess(Response response) {
        return this.performOnRequest(HandledProcessState.POST_PROCESSING, response, processor -> processor.postProcess(response));
    }

    public CompletableFuture<TerminatedResponse> terminate(PostProcessedResponse response) {
        return this.performOnRequest(HandledProcessState.TERMINATING, response, processor -> processor.terminate(response));
    }

    public void kill() {
        interrupt(this.arrivedRequest);
    }

    public CompletableFuture<Boolean> interrupt(Request request) {
        return this.engine.getActiveStation(this.stationRef)
                .thenCompose(station -> station.renege(new DefaultSubmittedRequest(request, this.stationRef, this.handleDate)))
                .thenApply(deleted -> {
                            if (deleted) {
                                return true;
                            } else {
                                HandledProcessStep step = this.currentStep.get();
                                return step.getTask().completeExceptionally(step.getState().applyWhenCancelled(step.getRequest()));
                            }
                        }
                );
    }
}
