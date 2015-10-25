package org.newdeal.core.engine.exception;

import org.newdeal.core.engine.Engine;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015
 * @version 0.0.1
 */
public class EngineNotDeclaredException extends RuntimeException {
    private final Engine engine;

    public EngineNotDeclaredException(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }
}
