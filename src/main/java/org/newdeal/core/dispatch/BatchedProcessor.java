package org.newdeal.core.dispatch;

import java.util.List;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 04/06/2015
 * @version 0.0.1
 */
public interface BatchedProcessor extends Processor {
    List<Processor> getServers();
}
