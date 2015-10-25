package org.newdeal.core.bean;

/**
 * -- UNSTABLE --
 *
 * A recordable entity is a stored item. Because a repository stores it, it has a specific ID
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public interface Recordable extends Referenceable {
    /**
     * @return ID to identify the instance in a given repository.
     */
    long getId();
}
