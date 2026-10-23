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
public class OrderBookEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "book";

    @JsonProperty("asset_id")
    private String assetId;

    private String market;

    private List<Level> bids;

    private List<Level> asks;

    private String timestamp;

    private String hash;

    // No-arg constructor required by Jackson for deserialization
    public OrderBookEvent() {
        super(EVENT_TYPE);
    }

    @Data
    public static class Level {

        private String price;

        private String size;
    }
}