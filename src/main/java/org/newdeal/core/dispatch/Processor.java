package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Response;
import org.newdeal.core.system.annotation.Blocking;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

/**
 * -- UNSTABLE --
 *
 * A processor is an entity that is able to manage requests. It defines actions to perform on each step of the
 * managing flow. Those actions can be blocking. No need here to wrap them into a non-blocking context.
 *
 * @author Frederick Vanderbeke
 * @since 27/05/2015
 * @version 0.0.1
 */
public interface Processor {
    /**
     * @return Which element represents this processor.
     */
    Referenceable getOwner();

    /**
     * Accepting step (to know if the processor is going to treat the given request).
     * <p>
     * Wrapped exception is <code>RefusedException</code>.
     *
     * @param request Dispatched request.
     * @return If right, the accepted request. If left, the exception (used as reason why it is refused).
     */
    @Blocking
    Either<Exception, AcceptedRequest> accept(DispatchedRequest request);

    /**
     * How the request is linked to the processor (used for instance to manage all the technical stuff to join
     * each member of the transaction and committing the conversation).
     * <p>
     * Wrapped exception is <code>DeniedException</code>.
     *
     * @param request Accepted request.
     * @return If right, the handshaked request. If left, the exception explaining why handshaking failed.
     */
    @Blocking
    Either<Exception, HandshakedRequest> handshake(AcceptedRequest request);

    /**
     * Pre processing step.
     *
     * @param request Handshaked request.
     * @return If right, data about the pre processed request. If left, the reason why it failed.
     */
    @Blocking
    Either<Exception, PreProcessedRequest> preProcess(HandshakedRequest request);

    /**
     * The main process step itself.
     *
     * @param request Pre processed request.
     * @return If right, information about the processed request (i.e. the response). If left, why it failed.
     */
    @Blocking
    Either<Exception, Response> process(PreProcessedRequest request);

    /**
     * The post process stuff.
     *
     * @param response the processed response.
     *
     * @return If right, post processed response data. If left, the reason of its failure.
     */
    @Blocking
    Either<Exception, PostProcessedResponse> postProcess(Response response);

    /**
     * The final step. Mainly used to clean extra stuff before starting a new request processing cycle.
     *
     * @param response Post processed response.
     *
     * @return the terminated data (if right). If failed, exception will be given as "left".
     */
    @Blocking
    Either<Exception, TerminatedResponse> terminate(PostProcessedResponse response);
}
