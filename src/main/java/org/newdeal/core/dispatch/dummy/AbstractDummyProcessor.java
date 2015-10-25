package org.newdeal.core.dispatch.dummy;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.dispatch.Processor;
import org.newdeal.core.dispatch.request.DefaultTerminatedResponse;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.dummy.*;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

/**
 * @author Frederick Vanderbeke
 * @version 0.0.1
 * @since 11/10/2015
 */
public abstract class AbstractDummyProcessor implements Processor {
    private final Referenceable owner;

    public AbstractDummyProcessor(final Referenceable owner) {
        this.owner = owner;
    }

    @Override
    public Referenceable getOwner() {
        return this.owner;
    }

    @Override
    public Either<Exception, AcceptedRequest> accept(DispatchedRequest request) {
        return Either.right(new DummyAcceptedRequest(request));
    }

    @Override
    public Either<Exception, HandshakedRequest> handshake(AcceptedRequest request) {
        return Either.right(new DummyHandshakedRequest(request));
    }

    @Override
    public Either<Exception, PreProcessedRequest> preProcess(HandshakedRequest request) {
        return Either.right(new DummyPreProcessedRequest(request));
    }

    @Override
    public Either<Exception, Response> process(PreProcessedRequest request) {
        return Either.right(new DummyResponse(request));
    }

    @Override
    public Either<Exception, PostProcessedResponse> postProcess(Response response) {
        return Either.right(new DummyPostProcessedResponse(response));
    }

    @Override
    public Either<Exception, TerminatedResponse> terminate(PostProcessedResponse response) {
        return Either.right(new DefaultTerminatedResponse(response));
    }
}
