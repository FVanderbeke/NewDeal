package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Model;
import org.newdeal.core.system.NbServers;
import org.newdeal.core.system.request.SubmittedRequest;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 14/09/2015
 * @version 0.0.1
 */
public abstract class AbstractActiveStation implements ActiveStation {

    /**
     * Object for which this instance is active.
     */
    private final Station origin;

    /**
     * For allocation process. A queue that is used to trigger the allocation process if a server is waiting for
     * request.
     */
    final BlockingQueue<PluggedProcessor> idleProcessors;
    /**
     * If this station is still active or not.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);


    AbstractActiveStation(final Station origin) {
        this.origin = origin;

        if (NbServers.isUnbound(getModel().getNbServers())) {
            this.idleProcessors = new PriorityBlockingQueue<>(10, getProcessorComparator());
        } else {
            this.idleProcessors = new PriorityBlockingQueue<>(getModel().getNbServers().getValue(), getProcessorComparator());
        }

    }


    protected abstract void innerStart();

    protected abstract void innerStop();

    @Override
    public ActiveStation init() {
        if (started.compareAndSet(false, true)) {
            innerStart();

            // TODO : add streams here.
        }
        return this;
    }

    @Override
    public void destroy() {
        if (started.compareAndSet(true, false)) {
            innerStop();
        }
    }


    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public Referenceable getOwner() {
        return origin.getOwner();
    }

    @Override
    public Comparator<SubmittedRequest> getRequestComparator() {
        return origin.getRequestComparator();
    }

    @Override
    public Comparator<PluggedProcessor> getProcessorComparator() {
        return origin.getProcessorComparator();
    }

    @Override
    public final Model getModel() {
        return origin.getModel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station that = (Station) o;
        return Objects.equals(getOwner(), that.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner());
    }

}
