package de.ti20.webhook.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GitHubPayloadTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void testCreation() {
        var payload = new GitHubPayload(new Repository("test/repo"), "main");
        assertEquals("test/repo", payload.repository().fullName());
        assertEquals("main", payload.ref());
    }

    @Test
    void testDeserialization() throws Exception {
        var json = """
            {
                "repository": {"full_name": "test/repo"},
                "ref": "refs/heads/main"
            }
            """;
        var payload = objectMapper.readValue(json, GitHubPayload.class);
        assertEquals("refs/heads/main", payload.ref());
        assertEquals("test/repo", payload.repository().fullName());
    }
}