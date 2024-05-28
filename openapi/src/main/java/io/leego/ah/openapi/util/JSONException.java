package io.leego.ah.openapi.util;

import java.io.Serial;

/**
 * @author Leego Yih
 */
public class JSONException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5266059128607574763L;

    /**
     * Constructs a new json exception with no detail message.
     */
    public JSONException() {
        super();
    }

    /**
     * Constructs a new json exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Constructs a new json exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new json exception with the cause.
     *
     * @param cause the cause.
     */
    public JSONException(Throwable cause) {
        super(cause);
    }
}