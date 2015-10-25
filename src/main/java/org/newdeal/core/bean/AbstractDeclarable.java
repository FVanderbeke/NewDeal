package org.newdeal.core.bean;

import java.time.Instant;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015
 * @version 0.0.1
 */
public abstract class AbstractDeclarable<R extends Referenceable> extends AbstractReferenceable implements Declarable {

    private final long id;

    private final Instant declarationDate;

    private final Instant lastDeactivationDate;

    public AbstractDeclarable(long id, String name, String label, String comment, Instant declarationDate) {
        this(id, name, label, comment, declarationDate, null);
    }

    public AbstractDeclarable(long id, String name, String label, String comment, Instant declarationDate, Instant lastDeactivationDate) {
        super(name, label, comment);
        this.id = id;
        this.declarationDate = declarationDate;
        this.lastDeactivationDate = lastDeactivationDate;
    }

    @Override
    public Instant getDeclarationDate() {
        return this.declarationDate;
    }

    @Override
    public Optional<Instant> getLastDeactivationDate() {
        return Optional.ofNullable(this.lastDeactivationDate);
    }

    @Override
    public long getId() {
        return this.id;
    }
}
