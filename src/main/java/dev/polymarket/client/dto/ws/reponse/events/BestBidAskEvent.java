package dev.polymarket.client.dto.ws.reponse.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestBidAskEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "best_bid_ask";

    @JsonProperty("asset_id")
    private String assetId;

    private String market;

    @JsonProperty("best_bid")
    private String bestBid;

    @JsonProperty("best_ask")
    private String bestAsk;

    private String spread;

    private String timestamp;

    public BestBidAskEvent() {
        super(EVENT_TYPE);
    }
}
