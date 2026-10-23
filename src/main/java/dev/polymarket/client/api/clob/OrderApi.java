package dev.polymarket.client.api.clob;

import dev.polymarket.client.api.AbstractApi;
import dev.polymarket.client.api.interceptor.AuthL2Interceptor;
import dev.polymarket.client.dto.PolymarketContext;
import dev.polymarket.client.dto.api.response.order.CancelOrderResponse;
import dev.polymarket.client.dto.api.response.order.CreateAndPlaceOrderResponse;
import dev.polymarket.client.dto.api.request.order.CancelOrderRequest;
import dev.polymarket.client.dto.api.request.order.UserOrderRequest;
import dev.polymarket.client.enums.system.SystemEnv;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * API client for order management on Polymarket CLOB.
 * <p>
 * Provides methods for creating, placing, and canceling orders.
 * Requires L2 (API key-based) authentication.
 */
public class OrderApi extends AbstractApi {

    private final PolymarketContext context;
    private final String owner;

    // Primary Constructor
    public OrderApi(
            PolymarketContext context,
            String owner,
            AuthL2Interceptor authL2Interceptor,
            String baseUrl
    ) {
        super(
                baseUrl,
                Collections.singletonList(authL2Interceptor)
        );
        this.context = context;
        this.owner = owner;
    }

    // Default Constructor mimicking Kotlin's default argument property fallback
    public OrderApi(PolymarketContext context, String owner, AuthL2Interceptor authL2Interceptor) {
        this(
                context,
                owner,
                authL2Interceptor,
                SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault() + "/order"
        );
    }

    /**
     * Creates and places an order on the Polymarket CLOB.
     *
     * @param userOrder User order request with order parameters.
     * @return CompletableFuture with CreateAndPlaceOrderResponse containing order result.
     */
    public CompletableFuture<CreateAndPlaceOrderResponse> createAndPlaceOrder(UserOrderRequest userOrder) {
        Object mappedBody = userOrder.toCreateAndPlaceOrderRequest(this.owner, this.context);
        return super.post(EMPTY_PATH, mappedBody, CreateAndPlaceOrderResponse.class);
    }

    /**
     * Cancels an existing order by its ID.
     *
     * @param orderId Unique identifier of the order to cancel.
     * @return CompletableFuture with CancelOrderResponse containing cancellation result.
     */
    public CompletableFuture<CancelOrderResponse> cancelOrder(String orderId) {
        CancelOrderRequest cancelBody = new CancelOrderRequest(orderId);
        return super.delete(EMPTY_PATH, cancelBody, CancelOrderResponse.class);
    }

    // --- Getters ---
    public PolymarketContext getContext() { return context; }
    public String getOwner() { return owner; }
}
