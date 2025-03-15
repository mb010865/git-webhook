package de.ti20.webhook.boundary;

import de.ti20.webhook.entity.GitOperationException;
import de.ti20.webhook.entity.WebhookEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class GitService {

    @Inject
    MeterRegistry meterRegistry;

    @Inject
    Logger LOG;

    public GitService(MeterRegistry meterRegistry, Logger LOG) {
        this.meterRegistry = meterRegistry;
        this.LOG = LOG;
    }

    public Response executeGitPull(String repoPath, WebhookEvent event) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "pull", "origin", event.branch())
                    .directory(Paths.get(repoPath).toFile());
            LOG.debugf("Executing git pull for %s in %s", event.repository(), repoPath);
            int exitCode = pb.start().waitFor();
            if (exitCode == 0) {
                meterRegistry.counter("webhook.pulls.success").increment();
                LOG.infof("Successfully pulled changes for %s", event.repository());
                return Response.ok("Pull executed for " + event.repository()).build();
            } else {
                meterRegistry.counter("webhook.pulls.failed", Tags.of("reason", "git_error")).increment();
                LOG.errorf("Git pull failed for %s with exit code %d", event.repository(), exitCode);
                throw new GitOperationException("Git pull failed with exit code: " + exitCode);
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            meterRegistry.counter("webhook.pulls.failed", Tags.of("reason", "system_error")).increment();
            LOG.errorf("Error executing pull for %s: %s", event.repository(), e.getMessage());
            throw new GitOperationException("Error executing pull: " + e.getMessage());
        }
    }
}