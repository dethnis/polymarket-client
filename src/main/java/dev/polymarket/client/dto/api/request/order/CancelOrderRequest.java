package dev.polymarket.client.dto.api.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelOrderRequest {

    @JsonProperty("orderID")
    private String orderId;

    // No-arg constructor required by Jackson for deserialization
    public CancelOrderRequest() {}

    public CancelOrderRequest(String orderId) {
        this.orderId = orderId;
    }

    // --- Getters and Setters ---

    @JsonProperty("orderID")
    public String getOrderId() { return orderId; }

    @JsonProperty("orderID")
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
