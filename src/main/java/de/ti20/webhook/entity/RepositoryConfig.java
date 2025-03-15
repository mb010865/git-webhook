package de.ti20.webhook.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ti20.webhook.entity.ConfigJson;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public record RepositoryConfig(Map<String, String> repositories, String targetBranch) {
    public static RepositoryConfig load(ObjectMapper mapper, Path configPath, Logger logger) {
        logger.infof("Loading config from %s", configPath);
        try (var lines = Files.lines(configPath, StandardCharsets.UTF_8)) {
            String json = lines.collect(Collectors.joining());
            var config = mapper.readValue(json, ConfigJson.class);
            var repos = new ConcurrentHashMap<>(config.repositories());
            var repositoryConfig = new RepositoryConfig(repos, config.branch() != null ? config.branch() : "main");
            logger.infof("Loaded %d repository mappings", repos.size());
            return repositoryConfig;
        } catch (IOException e) {
            logger.error("Failed to load config: " + e.getMessage());
            return new RepositoryConfig(new ConcurrentHashMap<>(), "main");
        }
    }
}