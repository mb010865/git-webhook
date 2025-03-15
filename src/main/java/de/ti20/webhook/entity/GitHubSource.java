package de.ti20.webhook.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ti20.webhook.boundary.WebhookResource;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public record GitHubSource(ObjectMapper objectMapper, String secret) implements WebhookSource {

    @Override
    public boolean verify(HttpHeaders headers, String payload) {
        String signature = headers.getHeaderString("X-Hub-Signature-256");
        if (signature == null) return false;
        return signature.equals("sha256=" + computeHmacSha256(payload));
    }

    @Override
    public WebhookEvent extractEvent(String payload) {
        try {
            var json = objectMapper.readValue(payload, GitHubPayload.class);
            return new WebhookEvent(json.repository().fullName().toLowerCase(), json.ref().replace("refs/heads/", ""));
        } catch (IOException e) {
            throw new PayloadParseException("Failed to parse GitHub payload", e);
        }
    }

    private String computeHmacSha256(String payload) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new SignatureException("Failed to compute signature", e);
        }
    }
}
