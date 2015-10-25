package org.newdeal.core.engine;

import org.newdeal.core.bean.Referenceable;

import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public abstract class AbstractDeclaredEngine<O extends Referenceable> extends AbstractEngine implements DeclaredEngine {

    private final long id;
    private final O origin;
    private final Instant declarationDate;
    private final Instant lastDeactivationDate;

    public AbstractDeclaredEngine(long id, final O origin, Instant declarationDate) {
        this(id, origin, declarationDate, null);
    }

    public AbstractDeclaredEngine(long id, O origin, Instant declarationDate, Instant deactivationDate) {
        super(origin.getName(), origin.getLabel(), origin.getComment().orElse(null));
        this.id = id;
        this.origin = origin;
        this.declarationDate = declarationDate;
        this.lastDeactivationDate = deactivationDate;
    }


    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return origin.getName();
    }

    @Override
    public String getLabel() {
        return origin.getLabel();
    }

    @Override
    public Optional<String> getComment() {
        return origin.getComment();
    }

    @Override
    public Instant getDeclarationDate() {
        return declarationDate;
    }

    @Override
    public Optional<Instant> getLastDeactivationDate() {
        return Optional.ofNullable(lastDeactivationDate);
    }
}
