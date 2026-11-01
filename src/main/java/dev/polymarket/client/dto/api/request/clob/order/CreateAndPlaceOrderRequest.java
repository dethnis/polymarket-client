package dev.polymarket.client.dto.api.request.clob.order;

import dev.polymarket.client.enums.OrderType;
import dev.polymarket.client.enums.TradeSide;
import dev.polymarket.client.utils.Eip712Model;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value // Makes class final, fields private and final, and generates getters
@Builder
public class CreateAndPlaceOrderRequest {
    @Builder.Default
    boolean deferExec = false;
    @Builder.Default
    boolean postOnly = false;
    Order order;
    String owner;
    OrderType orderType;

    /**
     * CLOB V2 order. Fees, taker and nonce are no longer part of the order.
     */
    @Data // Generates getters, setters, equals, hashCode, and toString
    @Builder
    public static class Order {
        long salt;
        String maker;
        String signer;
        String tokenId;
        String makerAmount;
        String takerAmount;
        TradeSide side;

        /**
         * Sent for GTD handling, but not part of the signed struct.
         */
        @Builder.Default
        String expiration = "0";

        int signatureType;

        /**
         * Unix milliseconds; replaces nonce for per-address order uniqueness.
         */
        String timestamp;

        @Builder.Default
        String metadata = Eip712Model.BYTES32_ZERO;

        /**
         * Builder code, zero unless attributing the order to a builder.
         */
        @Builder.Default
        String builder = Eip712Model.BYTES32_ZERO;

        @Builder.Default
        String signature = "";
    }
}