package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

public class PayloadParseException extends WebhookException {
    public PayloadParseException(String message, Throwable cause) {
        super(message, Response.Status.BAD_REQUEST);
        initCause(cause);
    }
}
