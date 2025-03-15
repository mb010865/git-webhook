package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

public class ValidationException extends WebhookException {
    public ValidationException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }
}
