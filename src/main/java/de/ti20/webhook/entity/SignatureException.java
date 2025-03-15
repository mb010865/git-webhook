package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

public class SignatureException extends WebhookException {
    public SignatureException(String message, Throwable cause) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}
