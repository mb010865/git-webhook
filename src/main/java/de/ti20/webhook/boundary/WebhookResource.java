package de.ti20.webhook.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ti20.webhook.entity.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Path("/")
public class WebhookResource {
    public static final Logger LOG = Logger.getLogger(WebhookResource.class);

    public static final String DEFAULT_CONFIG_PATH = "config/repos.json";

    @Inject
    @ConfigProperty(name = "webhook.config.path", defaultValue = DEFAULT_CONFIG_PATH)
    String configPath;

    @Inject
    @ConfigProperty(name = "webhook.secret", defaultValue = "default-secret")
    String secret;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MeterRegistry meterRegistry;

    private volatile RepositoryConfig config;

    @Inject
    GitService gitService;

    void onStart(@Observes StartupEvent event) {
        config = RepositoryConfig.load(objectMapper, Paths.get(configPath), LOG);
        meterRegistry.gauge("webhook.repositories.count", config.repositories().size());
        LOG.infof("Loaded %d repository mappings", config.repositories().size());
    }

    @POST
    public Response handleWebhook(HttpHeaders headers, String payload) {
        LOG.debug("Received webhook request");
        meterRegistry.counter("webhook.requests.total").increment();

        try {
            return identifySource(headers)
                    .map(source -> {
                        if (!source.verify(headers, payload)) {
                            meterRegistry.counter("webhook.requests.failed", Tags.of("reason", "auth")).increment();
                            throw new AuthenticationException("Invalid token or signature");
                        }
                        return processWebhook(source.extractEvent(payload));
                    })
                    .orElseThrow(() -> {
                        meterRegistry.counter("webhook.requests.failed", Tags.of("reason", "source")).increment();
                        return new AuthenticationException("No valid webhook source identified");
                    });
        } catch (WebhookException e) {
            LOG.errorf("Webhook processing failed: %s", e.getMessage());
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    private Optional<WebhookSource> identifySource(HttpHeaders headers) {
        return switch (headers.getHeaderString("X-Gitlab-Token") != null ? "gitlab" :
                headers.getHeaderString("X-Hub-Signature-256") != null ? "github" : "none") {
            case "gitlab" -> Optional.of(new GitLabSource(objectMapper, secret));
            case "github" -> Optional.of(new GitHubSource(objectMapper, secret));
            default -> Optional.empty();
        };
    }

    private Response processWebhook(WebhookEvent event) {
        LOG.infof("Processing event for %s, branch: %s", event.repository(), event.branch());
        String repoPath = config.repositories().get(event.repository());
        if (repoPath == null) {
            meterRegistry.counter("webhook.requests.failed", Tags.of("reason", "unknown_repo")).increment();
            throw new ValidationException("Unknown repository: " + event.repository());
        }
        if (!config.targetBranch().equals(event.branch())) {
            meterRegistry.counter("webhook.requests.ignored", Tags.of("reason", "branch")).increment();
            return Response.ok("Ignoring branch: " + event.branch() + " (expected: " + config.targetBranch() + ")").build();
        }
        return gitService.executeGitPull(repoPath, event);
    }
}