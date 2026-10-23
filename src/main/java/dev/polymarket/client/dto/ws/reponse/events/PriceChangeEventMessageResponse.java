package dev.polymarket.client.dto.ws.reponse.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Example Message:
 * <pre>
 * {@code
 * {
 * "market": "0x0270a8ce890ade23ace25488e1e00480189373e7fee45a6ae80f3cae9bd01e85",
 * "price_changes": [
 * {
 * "asset_id": "100257694793825545690880780160963130081516494584156260920620231154143979714267",
 * "price": "0.9",
 * "size": "105",
 * "side": "BUY",
 * "hash": "1abe6e3c87e3517f0c3ea89270a5edf0b4d7515c",
 * "best_bid": "0.9",
 * "best_ask": "0.93"
 * }
 * ],
 * "timestamp": "1763200441958",
 * "event_type": "price_change"
 * }
 * }
 * </pre>
 */
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceChangeEventMessageResponse extends EventMessageResponse {

    public static final String EVENT_TYPE = "price_change";

    /** Market id (hex). */
    private String market;

    /** List of individual price change entries. */
    @JsonProperty("price_changes")
    private List<PriceChange> priceChanges;

    /** Event timestamp in Unix millis. */
    private long timestamp;

    // No-arg constructor required by Jackson for deserialization
    public PriceChangeEventMessageResponse() {
        super(EVENT_TYPE);
    }

    // ==========================================
    // Inner/Nested PriceChange Class
    // ==========================================
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceChange {

        /** Asset id (large integer as string). */
        @JsonProperty("asset_id")
        private String assetId;

        /** Trade or quote price. */
        private BigDecimal price;

        /** Executed size (quantity). */
        private String size;

        /** Side of the change: BUY or SELL. */
        private String side;

        /** Transaction / update hash. */
        private String hash;

        /** Current best bid after the update. */
        @JsonProperty("best_bid")
        private BigDecimal bestBid;

        /** Current best ask after the update. */
        @JsonProperty("best_ask")
        private BigDecimal bestAsk;
    }
}
