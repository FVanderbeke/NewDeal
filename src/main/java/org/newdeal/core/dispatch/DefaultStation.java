package org.newdeal.core.dispatch;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Model;

import java.util.Objects;

/**
 * @author Frederick Vanderbeke
 * @since 11/08/2015
 * @version 0.0.1
 * -- UNSTABLE --
 */
public class DefaultStation implements Station {

    private final Model model;

    private final Referenceable owner;

    public DefaultStation(Model model, Referenceable owner) {
        this.model = model;
        this.owner = owner;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Referenceable getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultStation)) return false;
        DefaultStation that = (DefaultStation) o;
        return Objects.equals(getOwner(), that.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner());
    }
}
