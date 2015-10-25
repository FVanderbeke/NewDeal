package org.newdeal.core.bean.dummy;

import org.newdeal.core.bean.Activable;
import org.newdeal.core.bean.BeanType;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * -- UNSTABLE --
 * @author Frederick Vanderbeke
 * @since 29/07/2015
 * @version 0.0.1
 */
public class DummyActivable implements Activable {

    private final static AtomicLong seq = new AtomicLong(0);

    private final long id = seq.addAndGet(1);
    private BeanType type;
    private final String name;
    private final String label;

    private Instant declarationDate = Instant.now();
    private Instant lastActivationDate = Instant.now();

    public DummyActivable(BeanType type, String name, String label) {
        this.type = type;
        this.name = name;
        this.label = label;
    }

    @Override
    public Optional<Instant> getLastDeactivationDate() {
        return Optional.empty();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Optional<String> getComment() {
        return Optional.empty();
    }

    @Override
    public BeanType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Instant getDeclarationDate() {
        return declarationDate;
    }

    public void setDeclarationDate(Instant declarationDate) {
        this.declarationDate = declarationDate;
    }

    @Override
    public Instant getLastActivationDate() {
        return lastActivationDate;
    }

    public void setLastActivationDate(Instant lastActivationDate) {
        this.lastActivationDate = lastActivationDate;
    }
}
