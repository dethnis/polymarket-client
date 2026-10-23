package dev.polymarket.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Outcome {
    UP,
    DOWN;

    @JsonCreator
    public static Outcome of(String outcome) {
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome string value cannot be null");
        }
        return Outcome.valueOf(outcome.toUpperCase());
    }
}