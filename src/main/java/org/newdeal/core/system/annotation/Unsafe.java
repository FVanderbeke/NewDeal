package org.newdeal.core.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Frederick Vanderbeke
 * @since 08/09/2015.
 *
 * Marking unsafe method (that can cause some side effects.
 */

@Target(ElementType.METHOD)
public @interface Unsafe {
}
