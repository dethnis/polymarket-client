package dev.polymarket.client.dto.ws.reponse.events.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.polymarket.client.dto.ws.reponse.events.EventMessageResponse;
import dev.polymarket.client.enums.OrderEventType;
import dev.polymarket.client.enums.Outcome;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Example Message:
 * <pre>
 * {@code
 * {
 * "id": "0xdeca1466731736346fbc147c342faeb587e1aa0fbe627307ccd0950f3c9fa83f",
 * "owner": "4f3475be-0005-7089-406b-e56e0488e70d",
 * "market": "0xcfa2314621325a7cf1665dd46a7f39943c3f98f3523706b878a1893518587912",
 * "asset_id": "79896456028532955432035209353721094667908890089632877194159371137859471890538",
 * "side": "BUY",
 * "order_owner": "4f3475be-0005-7089-406b-e56e0488e70d",
 * "original_size": "5",
 * "size_matched": "0",
 * "price": "0.99",
 * "associate_trades": [],
 * "outcome": "Down",
 * "type": "PLACEMENT",
 * "created_at": "1764556003",
 * "expiration": "0",
 * "order_type": "GTC",
 * "status": "LIVE",
 * "maker_address": "0x17eb4Ad33e0FB336Ce6d7B6b7f4DC7C07dBAD615",
 * "timestamp": "1764556003008",
 * "event_type": "order"
 * }
 * }
 * </pre>
 */
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEventMessageResponse extends EventMessageResponse {

    public static final String EVENT_TYPE = "order";

    private String id;

    @JsonProperty("asset_id")
    private String assetId;

    private Outcome outcome;

    private OrderEventType type;

    // No-arg constructor required by Jackson for deserialization
    public OrderEventMessageResponse() {
        super(EVENT_TYPE);
    }

}
