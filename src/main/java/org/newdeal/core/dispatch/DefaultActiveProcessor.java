package org.newdeal.core.dispatch;

import org.newdeal.core.dispatch.exception.InactiveProcessorException;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 17/08/2015
 * @version 0.0.1
 */
public class DefaultActiveProcessor extends DefaultDeclaredProcessor implements ActiveProcessor {

    /**
     * Declared processor used to instantiate this current active one.
     */
    private final DeclaredProcessor origin;

    /**
     * State of this
     */
    private final AtomicReference<ActiveProcessor.State> currentState = new AtomicReference<>(State.IDLE);

    /**
     * Locks used to halt/pause the actions.
     */
    private final Lock internalLock = new ReentrantLock();

    /**
     * If the processor is now deactivated.
     */
    private final AtomicBoolean deactivated = new AtomicBoolean(false);

    private final AtomicReference<State> previousState = new AtomicReference<>();

    /**
     * @param processor declared processor to activate.
     */
    public DefaultActiveProcessor(DeclaredProcessor processor) {
        super(processor.getPid(), processor);
        this.origin = processor;
    }

    private CompletableFuture<Boolean> performFuture(Runnable runnable) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        CompletableFuture.runAsync(runnable::run).whenComplete((useless, error) -> {
            if (error != null) {
                result.completeExceptionally(error);
            } else {
                result.complete(true);
            }
        });

        return result;
    }

    @Override
    public DeclaredProcessor getDeclaredProcessor() {
        return origin;
    }

    @Override
    public CompletableFuture<Boolean> halt() {
        return performFuture(() -> {
            internalLock.lock();
            previousState.set(currentState.get());
            setCurrentState(State.HALTED);
        });
    }

    @Override
    public CompletableFuture<Boolean> resume() {
        return performFuture(() -> {
            if (!deactivated.get()) {
                currentState.compareAndSet(State.HALTED, previousState.get());
                internalLock.unlock();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deactivate() {
        return performFuture(() -> {
            deactivated.set(true);
            currentState.set(State.DEACTIVATING);
            internalLock.unlock();
        });
    }

    private <P extends DispatchedRequest, R> Either<Exception, R> perform(
            P request,
            Function<P, Either<Exception, R>> action,
            State before,
            State after) {

        internalLock.lock();
        setCurrentState(before);
        Either<Exception, R> result;

        if (deactivated.get()) {
            result = Either.left(new InactiveProcessorException(request));
        } else {
            result = action.apply(request);
        }

        result.fold(e -> {
                    internalLock.unlock();
                    halt();
                },
                r -> {
                    setCurrentState(after);
                    internalLock.unlock();
                });
        return result;
    }

    @Override
    public Either<Exception, AcceptedRequest> accept(DispatchedRequest request) {
        return perform(request, super::accept, State.ACCEPTING, State.ACCEPTED);
    }


    @Override
    public Either<Exception, HandshakedRequest> handshake(AcceptedRequest request) {
        return perform(request, super::handshake, State.HANDSHAKING, State.HANDSHAKED);
    }

    @Override
    public Either<Exception, PreProcessedRequest> preProcess(HandshakedRequest request) {
        return perform(request, super::preProcess, State.PRE_PROCESSING, State.PRE_PROCESSED);
    }

    @Override
    public Either<Exception, Response> process(PreProcessedRequest request) {
        return perform(request, super::process, State.PROCESSING, State.PROCESSED);
    }

    @Override
    public Either<Exception, PostProcessedResponse> postProcess(Response response) {
        return perform(response, super::postProcess, State.POST_PROCESSING, State.IDLE);
    }

    private void setCurrentState(State newState) {
        currentState.set(newState);
    }

}
