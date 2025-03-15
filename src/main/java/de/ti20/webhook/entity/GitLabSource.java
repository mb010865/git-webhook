package de.ti20.webhook.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ti20.webhook.boundary.WebhookResource;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

import java.io.IOException;

public record GitLabSource(ObjectMapper objectMapper, String secret) implements WebhookSource {

    @Override
    public boolean verify(HttpHeaders headers, String payload) {
        String token = headers.getHeaderString("X-Gitlab-Token");
        return token != null && secret.equals(token);
    }

    @Override
    public WebhookEvent extractEvent(String payload) {
        try {
            var json = objectMapper.readValue(payload, GitLabPayload.class);
            return new WebhookEvent(json.project().pathWithNamespace().toLowerCase(), json.ref().replace("refs/heads/", ""));
        } catch (IOException e) {
            throw new PayloadParseException("Failed to parse GitLab payload", e);
        }
    }
}
