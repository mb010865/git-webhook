package de.ti20.webhook.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public record ConfigJson(Map<String, String> repositories, String branch) {
}
