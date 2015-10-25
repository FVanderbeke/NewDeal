package org.newdeal.core.bean;

import java.util.Optional;

/**
 *
 * Simple object representing the reference above a referenceable instance.
 *
 * By definition, this implementation will return :
 * <ul>
 *  <li>no comment;</li>
 *  <li>no label.</li>
 * </ul>
 *
 * For memory optimization purpose. Because referenceable instances can be found thanks to their name & type only.
 *
 * @author Frederick Vanderbeke
 * @since 04/08/2015.
 * @version 0.0.1
 * -- UNSTABLE --
 */
public class BeanRef implements Referenceable {

    private final String name;
    private final BeanType type;

    public BeanRef(String name, BeanType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final BeanType getType() {
        return this.type;
    }

    @Override
    public final String getLabel() {
        return "";
    }

    @Override
    public final Optional<String> getComment() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "BeanRef{" +
                "name='" + this.name + '\'' +
                ", type=" + this.type +
                '}';
    }
}
