package org.newdeal.core.engine.local;

import org.newdeal.core.engine.AbstractDeclaredEngine;
import org.newdeal.core.engine.DeclaredDispatchEngine;
import org.newdeal.core.engine.DispatchEngine;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public class LocalDeclaredDispatchEngine extends AbstractDeclaredEngine<DispatchEngine> implements DeclaredDispatchEngine {

    public LocalDeclaredDispatchEngine(long id, DispatchEngine origin, Instant declarationDate) {
        this(id, origin, declarationDate, null);
    }

    public LocalDeclaredDispatchEngine(long id, DispatchEngine origin, Instant declarationDate, Instant deactivationDate) {
        super(id, origin, declarationDate, deactivationDate);
    }
}
