package dev.polymarket.client.dto.api.request.order;

import dev.polymarket.client.enums.OrderType;
import dev.polymarket.client.enums.TradeSide;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value // Makes class final, fields private and final, and generates getters
@Builder
public class CreateAndPlaceOrderRequest {
    @Builder.Default
    boolean deferExec = false;
    Order order;
    String owner;
    OrderType orderType;

    @Data // Generates getters, setters, equals, hashCode, and toString
    @Builder
    public static class Order {
        long salt;
        String maker;
        String signer;

        @Builder.Default
        String taker = "0x0000000000000000000000000000000000000000";

        String tokenId;
        String makerAmount;
        String takerAmount;
        String expiration;
        String nonce;

        @Builder.Default
        String feeRateBps = "0";

        TradeSide side;
        int signatureType;

        @Builder.Default
        String signature = "";
    }
}