package dev.polymarket.client.dto.ws.reponse.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketResolvedEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "market_resolved";

    private String id;

    private String question;

    private String market;

    private String slug;

    private String description;

    @JsonProperty("assets_ids")
    private List<String> assetIds;

    private List<String> outcomes;

    @JsonProperty("winning_asset_id")
    private String winningAssetId;

    @JsonProperty("winning_outcome")
    private String winningOutcome;

    @JsonProperty("event_message")
    private EventMessage eventMessage;

    private String timestamp;

    public MarketResolvedEvent() {
        super(EVENT_TYPE);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventMessage {

        private String id;

        private String ticker;

        private String slug;

        private String title;

        private String description;
    }

}
