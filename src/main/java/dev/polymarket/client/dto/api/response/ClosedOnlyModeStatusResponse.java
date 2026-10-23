package dev.polymarket.client.dto.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClosedOnlyModeStatusResponse {

    @JsonProperty("closed_only")
    private boolean closedOnly;

    // No-arg constructor required by Jackson for deserialization
    public ClosedOnlyModeStatusResponse() {}

    public ClosedOnlyModeStatusResponse(boolean closedOnly) {
        this.closedOnly = closedOnly;
    }

    // --- Getters and Setters ---

    @JsonProperty("closed_only")
    public boolean isClosedOnly() { return closedOnly; }

    @JsonProperty("closed_only")
    public void setClosedOnly(boolean closedOnly) { this.closedOnly = closedOnly; }
}
