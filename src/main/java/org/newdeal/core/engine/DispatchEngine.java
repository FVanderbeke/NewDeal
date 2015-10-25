package org.newdeal.core.engine;

import org.newdeal.core.bean.BeanType;

/**
 * -- UNSTABLE --
 *
 * Dispatching engine sends request to a given stage and dispatches it to a connected & available processor
 *
 * @author Frederick Vanderbeke
 * @since 05/06/2015
 * @version 0.0.1
 */
public interface DispatchEngine extends Engine {

    @Override
    default BeanType getType() {
        return BeanType.DISPATCH_ENGINE;
    }
}
