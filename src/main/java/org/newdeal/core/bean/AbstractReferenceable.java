package org.newdeal.core.bean;

import org.newdeal.core.engine.Engine;

import java.util.Objects;
import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015
 * @version 0.0.1
 */
public abstract class AbstractReferenceable implements Referenceable {
    private final String name;
    private String label;
    private String comment;

    public AbstractReferenceable(String name, String label) {
        this(name, label, null);
    }

    public AbstractReferenceable(String name, String label, String comment) {
        this.name = name;
        if (label == null || "".equals(label.trim())) {
            this.label = name;
        }
        this.comment = comment;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public Optional<String> getComment() {
        return Optional.ofNullable(this.comment);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Engine)) {
            return false;
        }
        Engine that = (Engine) o;
        return Objects.equals(this.getType(), that.getType()) &&
                Objects.equals(this.getName(), that.getName());
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getType(), this.getName());
    }
}
