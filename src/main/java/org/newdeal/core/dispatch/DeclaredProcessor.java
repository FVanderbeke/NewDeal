package org.newdeal.core.dispatch;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 12/06/2015
 * @version 0.0.1
 */
public interface DeclaredProcessor extends Processor {
    long getPid();
    Processor getInitialProcessor();
}
