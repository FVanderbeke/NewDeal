package org.newdeal.core.repository.exception;

import org.newdeal.core.engine.Engine;

/**
 * @author Frederick Vanderbeke
 * @since 23/06/2015.
 */
public class EngineAlreadyExistsException extends RuntimeException {
    private final Engine engine;

    public EngineAlreadyExistsException(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }
}
