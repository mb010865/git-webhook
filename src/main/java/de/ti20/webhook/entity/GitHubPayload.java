package de.ti20.webhook.entity;

public record GitHubPayload(Repository repository, String ref) {}