package org.newdeal.core.system;

import java.util.Objects;

/**
 * @author Frederick Vanderbeke
 * @since 12/08/2015.
 */
class DefaultNbServers implements NbServers {
    private final int value;
    public DefaultNbServers(int value) {
        if (value <= 0) {
            this.value = 0;
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
        if (!(o instanceof DefaultNbServers)) return false;
        DefaultNbServers that = (DefaultNbServers) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}