package dev.polymarket.client.dto.api.response.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * <pre>
 * {@code
 * {
 * "errorMsg":"",
 * "orderID":"0x8fc75ea5bf5eca24958d47fe02a78479134bce9b64e61cff74e6abd3226805f4",
 * "takingAmount":"",
 * "makingAmount":"",
 * "status":"live",
 * "success":true
 * }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAndPlaceOrderResponse {

    /**
     * boolean indicating if server-side err (success = false) -> server-side error
     */
    private boolean success;

    /**
     * error message in case of unsuccessful placement (in case success = false, e.g. client-side error, the reason is in errorMsg)
     */
    private String errorMsg;

    /**
     * id of order
     */
    @JsonProperty("orderID")
    private String orderId;

    /**
     * hash of settlement transaction order was marketable and triggered a match
     */
    private List<String> orderHashes;

    // No-arg constructor required by Jackson for deserialization
    public CreateAndPlaceOrderResponse() {}

    public CreateAndPlaceOrderResponse(boolean success, String errorMsg, String orderId, List<String> orderHashes) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.orderId = orderId;
        this.orderHashes = orderHashes;
    }

    // --- Getters and Setters ---

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    @JsonProperty("orderID")
    public String getOrderId() { return orderId; }

    @JsonProperty("orderID")
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public List<String> getOrderHashes() { return orderHashes; }
    public void setOrderHashes(List<String> orderHashes) { this.orderHashes = orderHashes; }
}
