package it.unibo.dronesecurity.userapplication.exceptions;

import it.unibo.dronesecurity.userapplication.shipping.courier.entities.Order;

/**
 * Exception thrown if {@link Order} is built using an empty product.
 */
public final class EmptyProductException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * Build the exception.
     */
    public EmptyProductException() {
        super("Product MUST NOT be null or empty!");
    }
}
