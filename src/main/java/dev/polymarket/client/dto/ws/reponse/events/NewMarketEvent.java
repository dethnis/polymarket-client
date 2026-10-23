package dev.polymarket.client.dto.ws.reponse.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewMarketEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "new_market";

    public NewMarketEvent() {
        super(EVENT_TYPE);
    }
}
