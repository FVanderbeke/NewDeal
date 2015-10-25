package org.newdeal.core.engine;

import org.newdeal.core.bean.AbstractReferenceable;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015
 * @version 0.0.1
 */
public abstract class AbstractEngine extends AbstractReferenceable implements Engine {

    public AbstractEngine(String name, String label, String comment) {
        super(name, label, comment);
    }
}
