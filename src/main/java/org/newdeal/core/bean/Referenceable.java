package org.newdeal.core.bean;

import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * Kind of instance having :
 * <ul>
 *     <li>A type.</li>
 *     <li>A name (defined once; can't be changed). Used as ID. Unique for a given type.</li>
 *     <li>A label (value that is displayed for this instance; can be updated).</li>
 *     <li>An optional comment.</li>
 * </ul>
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015.
 * @since 0.0.1
 */
public interface Referenceable {
    /**
     * @return Name of this instance. Name is an identifier. Must be unique for each type of instance (because used to
     * recognize this instance in the set of the ones defined for the given <code>type</code>).
     */
    String getName();

    /**
     * @return Label of this instance. Can't be empty. Can take any kind of value (except empty or null).
     */
    String getLabel();

    /**
     * @return Type of this instance. In NewDeal, any entity refers to a managed type of element.
     */
    BeanType getType();

    /**
     * @return An optional comment about the instance.
     */
    Optional<String> getComment();

    /**
     * @return A reference (used to compare and refer to this instance). Can be seen as a "pointer".
     */
    default BeanRef getRef() {
        return new BeanRef(getName(), getType());
    }
}
