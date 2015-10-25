package org.newdeal.core.dispatch.exception;

import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.system.Request;

import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 15/08/2015
 * @version 0.0.1
 */
abstract class AbstractDispatchException extends Exception {
        private final Optional<Request> request;
        private final Referenceable ownerRef;

        AbstractDispatchException(Referenceable ownerRef) {
            this(null, ownerRef);
        }

        AbstractDispatchException(Request request, Referenceable ownerRef) {
            this.request = Optional.ofNullable(request);
            this.ownerRef = ownerRef;
        }

        public Optional<Request> getRequest() {
            return request;
        }

        Referenceable getOwnerRef() {
            return ownerRef;
        }
}
