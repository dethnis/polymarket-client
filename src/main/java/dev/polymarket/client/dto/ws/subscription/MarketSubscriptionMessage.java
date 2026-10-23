package dev.polymarket.client.dto.ws.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketSubscriptionMessage {

    @JsonProperty("assets_ids")
    private List<String> assetsIds;

    private String type;

    // custom_feature_enabled: true to receive best_bid_ask, new_market, and market_resolved events.
    @JsonProperty("custom_feature_enabled")
    public Boolean customFeatureEnabled;

    public MarketSubscriptionMessage(List<String> assetsIds, String type, Boolean customFeatureEnabled) {
        this.assetsIds = assetsIds;
        this.type = type;
        this.customFeatureEnabled = customFeatureEnabled;
    }
}
