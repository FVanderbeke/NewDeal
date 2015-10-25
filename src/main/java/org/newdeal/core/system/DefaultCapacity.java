package org.newdeal.core.system;

import java.util.Objects;

/**
 * @author Frederick Vanderbeke
 * @since 12/08/2015.
 */
class DefaultCapacity implements Capacity {

    private final int value;

    public DefaultCapacity(int value) {
        if (value <= -1) {
            this.value = -1;
        } else {
            this.value = value;
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultCapacity)) return false;
        DefaultCapacity that = (DefaultCapacity) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
