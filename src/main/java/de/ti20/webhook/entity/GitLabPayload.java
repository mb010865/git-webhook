package de.ti20.webhook.entity;

public record GitLabPayload(Project project, String ref) {
}
