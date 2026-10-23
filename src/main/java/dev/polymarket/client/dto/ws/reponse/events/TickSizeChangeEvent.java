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
public class TickSizeChangeEvent extends EventMessageResponse {

    public static final String EVENT_TYPE = "tick_size_change";

    @JsonProperty("asset_id")
    private String assetId;

    private String market;

    @JsonProperty("old_tick_size")
    private String oldTickSize;

    @JsonProperty("new_tick_size")
    private String newTickSize;

    private String timestamp;

    public TickSizeChangeEvent() {
        super(EVENT_TYPE);
    }
}
