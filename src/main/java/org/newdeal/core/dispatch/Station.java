package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Model;
import org.newdeal.core.system.request.SubmittedRequest;

import java.util.Comparator;

/**
 * -- UNSTABLE --
 *
 * Represents a waiting queue declaration
 *
 * @author Frederick Vanderbeke
 * @since 27/05/2015
 * @version 0.0.1
 */
public interface Station {
    /**
     * Default comparator to sort arrived requests in the station (oldest one first).
     */
    Comparator<SubmittedRequest> DEFAULT_REQUEST_COMPARATOR = (s1, s2) -> s1.getCreationDate().compareTo(s2.getCreationDate());

    /**
     * Default comparator to sort processors that can handle requests on a station. Used to select a processor for
     * a given request if several processors are available for it.
     */
    Comparator<PluggedProcessor> DEFAULT_PROCESSOR_COMPARATOR =  (p1, p2) -> p1.getPlugDate().compareTo(p2.getPlugDate());

    /**
     * @return statistical model used upon this station.
     */
    Model getModel();

    /**
     * @return Element for which the station has been created.
     */
    Referenceable getOwner();

    /**
     * @return Which comparator is used to sort waiting requests.
     */
    default Comparator<SubmittedRequest> getRequestComparator() {
        return DEFAULT_REQUEST_COMPARATOR;
    }

    /**
     * @return The comparator used to sort processors.
     */
    default Comparator<PluggedProcessor> getProcessorComparator() {
        return DEFAULT_PROCESSOR_COMPARATOR;
    }


}
