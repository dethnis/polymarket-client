package dev.polymarket.client.dto.ws.reponse.events.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.polymarket.client.dto.ws.reponse.events.EventMessageResponse;
import dev.polymarket.client.enums.Outcome;
import dev.polymarket.client.enums.TradeSide;
import dev.polymarket.client.enums.TradeStatus;
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
 * "type": "TRADE",
 * "id": "81843817-fea1-4639-9dba-aa76844ef266",
 * "taker_order_id": "0x6f58bcbe1799141433494afc6af31528d96638a1a22ca2121725b728ac1c444e",
 * "market": "0xcfa2314621325a7cf1665dd46a7f39943c3f98f3523706b878a1893518587912",
 * "asset_id": "23448860976213154631725081346963907097252952426953513994184195559039864417078",
 * "side": "BUY",
 * "size": "500",
 * "fee_rate_bps": "0",
 * "price": "0.01",
 * "status": "MATCHED",
 * "match_time": "1764556051",
 * "last_update": "1764556051",
 * "outcome": "Up",
 * "owner": "57e0f6ab-1337-4a20-f4c2-f10b9a97aa4b",
 * "trade_owner": "57e0f6ab-1337-4a20-f4c2-f10b9a97aa4b",
 * "maker_address": "0x56fAC8560D88BFCc9a8aB4c9C29e1fcf826c8FC4",
 * "transaction_hash": "0x21ca109c74aa7f275a9ca8a9b89fff45696224e97035fb75d6c50290768e1938",
 * "bucket_index": 0,
 * "maker_orders": [
 * {
 * "order_id": "0x04526d5df14e4de183af48f701adc213fbd0e0250009ca0fd518c13ab1eb6354",
 * "owner": "132eccca-a738-b828-464d-598f3eff8ffa",
 * "maker_address": "0x926588D984e37C9bb974c5BedD6F38807F7a488b",
 * "matched_amount": "45.13",
 * "price": "0.99",
 * "fee_rate_bps": "0",
 * "asset_id": "79896456028532955432035209353721094667908890089632877194159371137859471890538",
 * "outcome": "Down",
 * "outcome_index": 0,
 * "side": "BUY"
 * }
 * ],
 * "trader_side": "MAKER",
 * "timestamp": "1764556051810",
 * "event_type": "trade"
 * }
 * }
 * </pre>
 */
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeEventMessageResponse extends EventMessageResponse {

    public static final String EVENT_TYPE = "trade";

    private String id;
    private String owner;

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("taker_order_id")
    private String takerOrderId;

    private Outcome outcome;
    private BigDecimal price;
    private BigDecimal size;
    private TradeStatus status;
    private TradeSide side;

    @JsonProperty("maker_orders")
    private List<MakerOrder> makerOrders;

    // No-arg constructor required by Jackson for deserialization
    public TradeEventMessageResponse() {
        super(EVENT_TYPE);
    }

    // ==========================================
    // Inner/Nested MakerOrder Class
    // ==========================================
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MakerOrder {

        @JsonProperty("order_id")
        private String orderId;

        private String owner;

        @JsonProperty("maker_address")
        private String makerAddress;

        @JsonProperty("matched_amount")
        private BigDecimal matchedAmount;

        private BigDecimal price;

        @JsonProperty("asset_id")
        private String assetId;

        private Outcome outcome;
        private TradeSide side;
    }
}
