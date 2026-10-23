package dev.polymarket.client.dto.ws.reponse.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.polymarket.client.dto.ws.reponse.events.user.OrderEventMessageResponse;
import dev.polymarket.client.dto.ws.reponse.events.user.TradeEventMessageResponse;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event_type",
        visible = true,
        defaultImpl = EventMessageResponse.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderBookEvent.class, name = OrderBookEvent.EVENT_TYPE),
        @JsonSubTypes.Type(value = PriceChangeEventMessageResponse.class, name = PriceChangeEventMessageResponse.EVENT_TYPE),
        @JsonSubTypes.Type(value = TickSizeChangeEvent.class, name = TickSizeChangeEvent.EVENT_TYPE),
        @JsonSubTypes.Type(value = LastTradePriceEvent.class, name = LastTradePriceEvent.EVENT_TYPE),
        // Requires custom_feature_enabled: true
        @JsonSubTypes.Type(value = BestBidAskEvent.class, name = BestBidAskEvent.EVENT_TYPE),
        @JsonSubTypes.Type(value = NewMarketEvent.class, name = NewMarketEvent.EVENT_TYPE),
        @JsonSubTypes.Type(value = MarketResolvedEvent.class, name = MarketResolvedEvent.EVENT_TYPE),
        // user responses
        @JsonSubTypes.Type(value = TradeEventMessageResponse.class, name = TradeEventMessageResponse.EVENT_TYPE),
        @JsonSubTypes.Type(value = OrderEventMessageResponse.class, name = OrderEventMessageResponse.EVENT_TYPE)
})
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMessageResponse {

    @JsonProperty("event_type")
    private String eventType;

    public EventMessageResponse(String eventType) {
        this.eventType = eventType;
    }

}

