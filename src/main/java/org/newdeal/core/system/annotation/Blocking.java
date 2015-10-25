package org.newdeal.core.system.annotation;

/**
 * @author Frederick Vanderbeke
 * @since 05/09/2015.
 *
 * To mark methods as blocking. For documentation convenience.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface Blocking {
}
