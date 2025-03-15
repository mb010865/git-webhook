package de.ti20.webhook.entity;

import jakarta.ws.rs.core.HttpHeaders;

public sealed interface WebhookSource permits GitHubSource, GitLabSource {
    boolean verify(HttpHeaders headers, String payload);

    WebhookEvent extractEvent(String payload);
}
