package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 21/08/2015
 * @version 0.0.1
 */
public class DefaultPluggedProcessor implements PluggedProcessor {


    private final ActiveProcessor processor;

    private final Referenceable stationRef;

    private final long token;

    private final Instant plugDate;

    public DefaultPluggedProcessor(ActiveProcessor processor, final Referenceable stationRef, long token, Instant plugDate) {
        this.processor = processor;
        this.stationRef = stationRef;
        this.plugDate = plugDate;
        this.token = token;
    }

    @Override
    public Instant getPlugDate() {
        return plugDate;
    }

    @Override
    public long getAllocationToken() {
        return this.token;
    }

    @Override
    public Referenceable getStationRef() {
        return this.stationRef;
    }

    @Override
    public DeclaredProcessor getDeclaredProcessor() {
        return processor.getDeclaredProcessor();
    }

    @Override
    public CompletableFuture<Boolean> halt() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> resume() {
        return processor.resume();
    }

    @Override
    public CompletableFuture<Boolean> deactivate() {
        return null;
    }

    @Override
    public long getPid() {
        return processor.getPid();
    }

    @Override
    public Processor getInitialProcessor() {
        return processor.getInitialProcessor();
    }

    @Override
    public Referenceable getOwner() {
        return processor.getOwner();
    }

    @Override
    public Either<Exception, AcceptedRequest> accept(DispatchedRequest request) {
        return processor.accept(request);
    }

    @Override
    public Either<Exception, HandshakedRequest> handshake(AcceptedRequest request) {
        return processor.handshake(request);
    }

    @Override
    public Either<Exception, PreProcessedRequest> preProcess(HandshakedRequest request) {
        return processor.preProcess(request);
    }

    @Override
    public Either<Exception, Response> process(PreProcessedRequest request) {
        return processor.process(request);
    }

    @Override
    public Either<Exception, PostProcessedResponse> postProcess(Response response) {
        return processor.postProcess(response);
    }

    @Override
    public PluggedProcessor copy(ActiveProcessor updatedValue) {
        return new DefaultPluggedProcessor(updatedValue, stationRef, token, plugDate);
    }

    @Override
    public Either<Exception, TerminatedResponse> terminate(PostProcessedResponse response) {
        return processor.terminate(response);
    }
}
