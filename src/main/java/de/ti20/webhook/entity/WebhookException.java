package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

// Custom exceptions
public class WebhookException extends RuntimeException {
    private final Response.Status status;

    public WebhookException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
