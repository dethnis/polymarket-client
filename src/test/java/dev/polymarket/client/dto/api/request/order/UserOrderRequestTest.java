package dev.polymarket.client.dto.api.request.order;

import dev.polymarket.client.dto.PolymarketContext;
import dev.polymarket.client.dto.api.request.clob.order.CreateAndPlaceOrderRequest;
import dev.polymarket.client.dto.api.request.clob.order.UserOrderRequest;
import dev.polymarket.client.dto.api.response.gamma.MarketResponse;
import dev.polymarket.client.enums.OrderType;
import dev.polymarket.client.enums.SignatureType;
import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.enums.TradeSide;
import dev.polymarket.client.utils.Eip712Model;
import dev.polymarket.client.utils.Eip712Signer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserOrderRequestTest {

    // Public Hardhat test accounts, never hold funds
    private static final String PRIVATE_KEY = "0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80";
    private static final String SIGNER = "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266";
    private static final String FUNDER = "0x70997970C51812dc3A010C7d01b50e0d17dc79C8";
    private static final String OWNER = "api-key-uuid";
    private static final String ASSET_ID = "123";

    private static final PolymarketContext CONTEXT =
            new PolymarketContext(137, SIGNER, PRIVATE_KEY, 0, FUNDER, SignatureType.POLY_GNOSIS_SAFE);

    private static CreateAndPlaceOrderRequest buyRequest(boolean negRisk) {
        return new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal("0.3"), new BigDecimal("5"),
                TickSize.TICK_0_01, negRisk)
                .toCreateAndPlaceOrderRequest(OWNER, CONTEXT);
    }

    @Test
    void buildsV2Order() {
        long before = System.currentTimeMillis();
        CreateAndPlaceOrderRequest request = buyRequest(false);
        long after = System.currentTimeMillis();
        CreateAndPlaceOrderRequest.Order order = request.getOrder();

        assertAll(
                () -> assertEquals(OWNER, request.getOwner()),
                () -> assertEquals(OrderType.GTC, request.getOrderType()),
                () -> assertFalse(request.isDeferExec()),
                () -> assertFalse(request.isPostOnly()),
                () -> assertEquals(FUNDER, order.getMaker()),
                () -> assertEquals(SIGNER, order.getSigner()),
                () -> assertEquals(ASSET_ID, order.getTokenId()),
                // BUY 5 shares @ 0.3: pay 1.5 collateral, receive 5 shares (6 decimals)
                () -> assertEquals("1500000", order.getMakerAmount()),
                () -> assertEquals("5000000", order.getTakerAmount()),
                () -> assertEquals(TradeSide.BUY, order.getSide()),
                () -> assertEquals("0", order.getExpiration()),
                () -> assertEquals(SignatureType.POLY_GNOSIS_SAFE.getIndex(), order.getSignatureType()),
                () -> assertEquals(Eip712Model.BYTES32_ZERO, order.getMetadata()),
                () -> assertEquals(Eip712Model.BYTES32_ZERO, order.getBuilder()),
                () -> assertTrue(order.getSalt() > 0),
                () -> {
                    long timestamp = Long.parseLong(order.getTimestamp());
                    assertTrue(timestamp >= before && timestamp <= after, "timestamp should be current unix millis");
                }
        );
    }

    @Test
    void sellOrderSwapsMakerAndTakerAmounts() {
        CreateAndPlaceOrderRequest.Order order =
                new UserOrderRequest(ASSET_ID, TradeSide.SELL, new BigDecimal("0.3"), new BigDecimal("5"),
                        TickSize.TICK_0_01, false)
                        .toCreateAndPlaceOrderRequest(OWNER, CONTEXT)
                        .getOrder();

        assertEquals("5000000", order.getMakerAmount());
        assertEquals("1500000", order.getTakerAmount());
    }

    @Test
    void signatureCoversOrderForSelectedExchange() {
        CreateAndPlaceOrderRequest.Order standard = buyRequest(false).getOrder();
        CreateAndPlaceOrderRequest.Order negRisk = buyRequest(true).getOrder();

        // ECDSA signing in web3j is deterministic (RFC 6979), so re-signing must reproduce the signature
        assertEquals(standard.getSignature(),
                Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, standard, false));
        assertEquals(negRisk.getSignature(),
                Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, negRisk, true));
        assertNotEquals(negRisk.getSignature(),
                Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, negRisk, false));
    }

    @Test
    void serializesToV2WireFormat() {
        JsonNode body = new ObjectMapper().valueToTree(buyRequest(false));
        JsonNode order = body.get("order");

        Set<String> orderFields = new HashSet<>(order.propertyNames());

        assertAll(
                () -> assertEquals(Set.of("deferExec", "postOnly", "order", "owner", "orderType"),
                        new HashSet<>(body.propertyNames())),
                () -> assertEquals(Set.of("salt", "maker", "signer", "tokenId", "makerAmount", "takerAmount",
                        "side", "expiration", "signatureType", "timestamp", "metadata", "builder", "signature"),
                        orderFields),
                () -> assertTrue(order.get("salt").isNumber()),
                () -> assertTrue(order.get("signatureType").isNumber()),
                () -> assertEquals("BUY", order.get("side").asString()),
                () -> assertTrue(order.get("timestamp").isString()),
                () -> assertEquals("GTC", body.get("orderType").asString())
        );
    }

    @Test
    void rejectsPriceOutOfRange() {
        for (String price : new String[]{"0.001", "1.0", "1.01"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal(price), BigDecimal.ONE,
                            TickSize.TICK_0_01, false),
                    price);
        }
    }

    @Test
    void priceRangeFollowsTickSize() {
        BigDecimal price = new BigDecimal("0.999");

        assertThrows(IllegalArgumentException.class,
                () -> new UserOrderRequest(ASSET_ID, TradeSide.BUY, price, BigDecimal.ONE, TickSize.TICK_0_01, false));
        assertEquals(TickSize.TICK_0_001,
                new UserOrderRequest(ASSET_ID, TradeSide.BUY, price, BigDecimal.ONE, TickSize.TICK_0_001, false)
                        .getTickSize());
    }

    @Test
    void rejectsMissingTickSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal("0.5"), BigDecimal.ONE, null, false));
    }

    @Test
    void roundsAmountsForMarketTickSize() {
        CreateAndPlaceOrderRequest.Order order =
                new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal("0.123"), new BigDecimal("10"),
                        TickSize.TICK_0_001, false)
                        .toCreateAndPlaceOrderRequest(OWNER, CONTEXT)
                        .getOrder();

        assertEquals("1230000", order.getMakerAmount());
        assertEquals("10000000", order.getTakerAmount());
    }

    private static MarketResponse market(String tickSize, Boolean negRisk) {
        MarketResponse market = new MarketResponse();
        market.setSlug("test-market");
        market.setClobTokenIds("[\"" + ASSET_ID + "\", \"456\"]");
        market.setOrderPriceMinTickSize(tickSize != null ? new BigDecimal(tickSize) : null);
        market.setNegRisk(negRisk);
        return market;
    }

    @Test
    void forMarketTakesTickSizeAndNegRiskFromMarketData() {
        UserOrderRequest request = UserOrderRequest.forMarket(
                market("0.001", true), ASSET_ID, TradeSide.BUY, new BigDecimal("0.123"), BigDecimal.TEN);

        assertAll(
                () -> assertEquals(TickSize.TICK_0_001, request.getTickSize()),
                () -> assertTrue(request.isNegRisk()),
                () -> assertEquals(ASSET_ID, request.getAssetId()),
                () -> assertEquals(OrderType.GTC, request.getOrderType()),
                () -> assertEquals("0", request.getExpiration())
        );
    }

    @Test
    void forMarketKeepsOrderTypeAndExpiration() {
        UserOrderRequest request = UserOrderRequest.forMarket(market("0.01", false), ASSET_ID, TradeSide.SELL,
                new BigDecimal("0.5"), BigDecimal.ONE, OrderType.GTD, "1900000000");

        assertEquals(OrderType.GTD, request.getOrderType());
        assertEquals("1900000000", request.getExpiration());
    }

    @Test
    void forMarketValidatesPriceAgainstMarketTickSize() {
        assertThrows(IllegalArgumentException.class, () -> UserOrderRequest.forMarket(
                market("0.01", false), ASSET_ID, TradeSide.BUY, new BigDecimal("0.999"), BigDecimal.ONE));
    }

    @Test
    void forMarketRejectsTokenOfAnotherMarket() {
        assertThrows(IllegalArgumentException.class, () -> UserOrderRequest.forMarket(
                market("0.01", false), "999", TradeSide.BUY, new BigDecimal("0.5"), BigDecimal.ONE));
    }

    @Test
    void forMarketRejectsIncompleteMarketData() {
        assertThrows(IllegalArgumentException.class, () -> UserOrderRequest.forMarket(
                market(null, false), ASSET_ID, TradeSide.BUY, new BigDecimal("0.5"), BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> UserOrderRequest.forMarket(
                market("0.01", null), ASSET_ID, TradeSide.BUY, new BigDecimal("0.5"), BigDecimal.ONE));
    }

    @Test
    void rejectsNonPositiveSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal("0.5"), BigDecimal.ZERO,
                        TickSize.TICK_0_01, false));
    }

    @Test
    void rejectsMissingFunderAddress() {
        PolymarketContext noFunder =
                new PolymarketContext(137, SIGNER, PRIVATE_KEY, 0, "", SignatureType.POLY_GNOSIS_SAFE);
        UserOrderRequest request =
                new UserOrderRequest(ASSET_ID, TradeSide.BUY, new BigDecimal("0.3"), new BigDecimal("5"),
                        TickSize.TICK_0_01, false);

        assertThrows(IllegalArgumentException.class, () -> request.toCreateAndPlaceOrderRequest(OWNER, noFunder));
    }
}
