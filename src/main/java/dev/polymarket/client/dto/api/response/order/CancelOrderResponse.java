package dev.polymarket.client.dto.api.response.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response received when attempting to cancel orders on Polymarket CLOB.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelOrderResponse {

    /**
     * list of canceled orders
     */
    private List<String> canceled;

    /**
     * order id -> reason map that explains why that order couldn’t be canceled
     */
    @JsonProperty("not_canceled")
    private Object notCanceled;

    // No-arg constructor required by Jackson for deserialization
    public CancelOrderResponse() {}

    public CancelOrderResponse(List<String> canceled, Object notCanceled) {
        this.canceled = canceled;
        this.notCanceled = notCanceled;
    }

    // --- Getters and Setters ---

    public List<String> getCanceled() { return canceled; }
    public void setCanceled(List<String> canceled) { this.canceled = canceled; }

    @JsonProperty("not_canceled")
    public Object getNotCanceled() { return notCanceled; }

    @JsonProperty("not_canceled")
    public void setNotCanceled(Object notCanceled) { this.notCanceled = notCanceled; }
}
