package dev.polymarket.client.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ApiException extends RuntimeException {

    private final HttpResponseStatus status;

    /**
     * Primary constructor with explicit message and status.
     */
    public ApiException(String message, HttpResponseStatus status) {
        super(message);
        this.status = status != null ? status : HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Overloaded constructor mimicking Kotlin's default argument fallback
     * to INTERNAL_SERVER_ERROR.
     */
    public ApiException(String message) {
        this(message, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Getter ---

    public HttpResponseStatus getStatus() {
        return status;
    }
}
