package de.ti20.webhook.entity;

import jakarta.ws.rs.core.Response;

public class GitOperationException extends WebhookException {
    public GitOperationException(String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }
}
