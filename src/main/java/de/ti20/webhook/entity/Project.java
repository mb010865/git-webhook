package de.ti20.webhook.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Project(@JsonProperty("path_with_namespace") String pathWithNamespace) {
}
