package de.ti20.webhook.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Repository(@JsonProperty("full_name") String fullName) {}