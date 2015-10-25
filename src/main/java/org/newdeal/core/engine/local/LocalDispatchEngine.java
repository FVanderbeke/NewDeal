package org.newdeal.core.engine.local;

import org.newdeal.core.bean.AbstractReferenceable;
import org.newdeal.core.engine.DispatchEngine;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 07/06/2015
 * @version 0.0.1
 */
public class LocalDispatchEngine extends AbstractReferenceable implements DispatchEngine {

    public LocalDispatchEngine(String name, String label, String comment) {
        super(name, label, comment);
    }

}
