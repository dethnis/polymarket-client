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
public class LastTradePriceEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "last_trade_price";

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("fee_rate_bps")
    private String feeRateBps;

    private String market;

    private String price;

    private String side;

    private String size;

    private String timestamp;

    public LastTradePriceEvent() {
        super(EVENT_TYPE);
    }
}
