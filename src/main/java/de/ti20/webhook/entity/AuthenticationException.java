package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

public class AuthenticationException extends WebhookException {
    public AuthenticationException(String message) {
        super(message, Response.Status.FORBIDDEN);
    }
}
