package dev.polymarket.client.dto.api.response.clob.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <pre>{@code
 * {"neg_risk": false}
 * }</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NegRiskResponse {

    @JsonProperty("neg_risk")
    private boolean negRisk;

    // No-arg constructor required by Jackson for deserialization
    public NegRiskResponse() {}

    public NegRiskResponse(boolean negRisk) {
        this.negRisk = negRisk;
    }

    // --- Getters and Setters ---

    @JsonProperty("neg_risk")
    public boolean isNegRisk() { return negRisk; }

    @JsonProperty("neg_risk")
    public void setNegRisk(boolean negRisk) { this.negRisk = negRisk; }
}
