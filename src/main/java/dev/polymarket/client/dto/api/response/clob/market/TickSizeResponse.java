package dev.polymarket.client.dto.api.response.clob.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * <pre>{@code
 * {"minimum_tick_size": 0.001}
 * }</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickSizeResponse {

    @JsonProperty("minimum_tick_size")
    private BigDecimal minimumTickSize;

    // No-arg constructor required by Jackson for deserialization
    public TickSizeResponse() {}

    public TickSizeResponse(BigDecimal minimumTickSize) {
        this.minimumTickSize = minimumTickSize;
    }

    // --- Getters and Setters ---

    @JsonProperty("minimum_tick_size")
    public BigDecimal getMinimumTickSize() { return minimumTickSize; }

    @JsonProperty("minimum_tick_size")
    public void setMinimumTickSize(BigDecimal minimumTickSize) { this.minimumTickSize = minimumTickSize; }
}
