package org.newdeal.core.bean;

import java.time.Instant;

/**
 * -- UNSTABLE --
 *
 * Entity that is activated
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015.
 * @version 0.0.1
 */
public abstract class AbstractActivable<D extends Declarable> extends AbstractDeclarable<D> implements Activable {

    /**
     * When this instance has been activated.
     */
    private final Instant activationDate;

    /**
     * Default constructor.
     *
     * @param id                   Entity ID.
     * @param name                 Name of this entity.
     * @param label                Label of this entity.
     * @param comment              Comment of this entity.
     * @param declarationDate      When it has been declared.
     * @param lastDeactivationDate last time it has been deactivated.
     * @param activationDate       It's activation date.
     */
    public AbstractActivable(long id, String name, String label, String comment, Instant declarationDate, Instant lastDeactivationDate, Instant activationDate) {
        super(id, name, label, comment, declarationDate, lastDeactivationDate);
        this.activationDate = activationDate;
    }

    @Override
    public Instant getLastActivationDate() {
        return this.activationDate;
    }

}
