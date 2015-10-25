package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 14/08/2015
 * @version 0.0.1
 */
public class DefaultDeclaredProcessor implements DeclaredProcessor {

    private final long pid;

    private final Processor origin;

    public DefaultDeclaredProcessor(long pid, Processor origin) {
        this.pid = pid;
        this.origin = origin;
    }

    @Override
    public long getPid() {
        return pid;
    }

    @Override
    public Processor getInitialProcessor() {
        return origin;
    }

    @Override
    public Referenceable getOwner() {
        return origin.getOwner();
    }

    @Override
    public Either<Exception, AcceptedRequest> accept(DispatchedRequest request) {
        return origin.accept(request);
    }

    @Override
    public Either<Exception, HandshakedRequest> handshake(AcceptedRequest request) {
        return origin.handshake(request);
    }

    @Override
    public Either<Exception, PreProcessedRequest> preProcess(HandshakedRequest request) {
        return origin.preProcess(request);
    }

    @Override
    public Either<Exception, Response> process(PreProcessedRequest request) {
        return origin.process(request);
    }

    @Override
    public Either<Exception, PostProcessedResponse> postProcess(Response response) {
        return origin.postProcess(response);
    }

    @Override
    public Either<Exception, TerminatedResponse> terminate(PostProcessedResponse response) {
        return origin.terminate(response);
    }
}
