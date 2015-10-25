package org.newdeal.core.system.request;

import org.newdeal.core.system.Request;

import java.util.Objects;

/**
 * @author Frederick Vanderbeke
 * @since 24/09/2015.
 *
 * Used to declare how requests can be compared.
 */
public abstract class AbstractComparableRequest implements Request, Comparable<Request> {

    @Override
    public int compareTo(Request o) {
        if (o == null) {
            return 1;
        }
        long result = this.getId() - this.getId();

        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractComparableRequest)) return false;
        AbstractComparableRequest that = (AbstractComparableRequest) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
