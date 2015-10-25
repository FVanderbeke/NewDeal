package org.newdeal.core.dispatch.request;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.request.AbstractComparableRequest;
import org.newdeal.core.system.request.PostProcessedResponse;
import org.newdeal.core.system.request.TerminatedResponse;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 25/08/2015
 * @version 0.0.1
 */
public class DefaultTerminatedResponse extends AbstractComparableRequest implements TerminatedResponse {

    private final PostProcessedResponse origin;

    private final Instant terminationDate;

    public DefaultTerminatedResponse(PostProcessedResponse response) {
        this.origin = response;
        this.terminationDate = response.getClock().instant();
    }

    @Override
    public long getId() {
        return this.origin.getId();
    }

    @Override
    public Instant getHandshakeDate() {
        return origin.getHandshakeDate();
    }

    @Override
    public Instant getTerminationDate() {
        return terminationDate;
    }

    @Override
    public Instant getPostProcessDate() {
        return origin.getPostProcessDate();
    }

    @Override
    public Instant getResponseDate() {
        return origin.getResponseDate();
    }

    @Override
    public Instant getPreProcessDate() {
        return origin.getPreProcessDate();
    }

    @Override
    public Instant getAcceptDate() {
        return origin.getAcceptDate();
    }

    @Override
    public long getTicket() {
        return origin.getTicket();
    }

    @Override
    public Instant getDispatchDate() {
        return origin.getDispatchDate();
    }

    @Override
    public Referenceable getProcessorRef() {
        return origin.getProcessorRef();
    }

    @Override
    public Instant getSubmissionDate() {
        return origin.getSubmissionDate();
    }

    @Override
    public Instant getHandleDate() {
        return origin.getHandleDate();
    }

    @Override
    public Referenceable getStationRef() {
        return origin.getStationRef();
    }

    @Override
    public Clock getClock() {
        return origin.getClock();
    }

    @Override
    public Instant getCreationDate() {
        return origin.getCreationDate();
    }

    @Override
    public Optional<Request> getOrigin() {
        return Optional.of(origin);
    }
}
