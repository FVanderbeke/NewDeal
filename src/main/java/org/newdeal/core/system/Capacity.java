package org.newdeal.core.system;

/**
 * @author Frederick Vanderbeke
 * @since 12/08/2015.
 */
public interface Capacity {

    int getValue();

    static Capacity fixed(int capacity) {
        if (capacity == -1) {
            return Constants.INFINITE;
        } else if (capacity == 0) {
            return Constants.ZERO;
        } else {
            return new DefaultCapacity(capacity);
        }
    }

    static Capacity zero() {
        return Constants.ZERO;
    }

    static Capacity infinite() {
        return Constants.INFINITE;
    }

    static boolean isZero(Capacity capacity) {
        return Constants.ZERO.equals(capacity);
    }

    static boolean isFixed(Capacity capacity) {
        return !isZero(capacity) && !isInfinite(capacity);
    }

    static boolean isInfinite(Capacity capacity) {
        return Constants.INFINITE.equals(capacity);
    }

    static Capacity copy(Capacity capacity) {
        return new DefaultCapacity(capacity.getValue());
    }

}
