package org.newdeal.core.system;

/**
 * @author Frederick Vanderbeke
 * @since 12/08/2015.
 *
 * Why "c" ? Because it is the character used to represent the number of servers in the Kendall's notation.
 */
public interface NbServers {
    int getValue();

    static NbServers c(int value) {
        if (value < 0) {
            return Constants.UNBOUND;
        } else {
            return new DefaultNbServers(value);
        }
    }

    static NbServers unbound() {
        return Constants.UNBOUND;
    }

    static boolean isUnbound(NbServers c) {
        return Constants.UNBOUND.equals(c);
    }

    static NbServers copy(NbServers c) {
        return new DefaultNbServers(c.getValue());
    }
}
