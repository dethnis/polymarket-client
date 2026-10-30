package dev.polymarket.client.utils;

import dev.polymarket.client.dto.api.request.order.CreateAndPlaceOrderRequest;
import dev.polymarket.client.enums.TradeSide;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Expected signatures were produced independently with eth-account 0.13.7 (Python), using the
 * CLOB V2 typed data from Polymarket's py-clob-client-v2 (domain "Polymarket CTF Exchange" v2,
 * 11-field Order struct). A mismatch means the signed struct or domain drifted from V2.
 */
class Eip712SignerTest {

    // Public Hardhat test account #0, never holds funds
    private static final String PRIVATE_KEY = "0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80";
    private static final String SIGNER = "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266";
    private static final String MAKER = "0x70997970C51812dc3A010C7d01b50e0d17dc79C8";

    private static final String EXPECTED_STANDARD_SIGNATURE =
            "0x45b30157249b90f08e7a491216339b0d22d52b115a1c17b0fc58a4354c59021f"
            + "6502f120e1dbaea705a54fdead21a74c8d747e8b5b23df765381ee039c8683c51c";
    private static final String EXPECTED_NEG_RISK_SIGNATURE =
            "0x8668ce4d38848c18ab4631db6bac2d2e931730b6a2528a51319918b295044658"
            + "2b0df78f51372c4982c14d3654a32df348a53d1bb98fa139fdb9cc6fd3f8cacc1b";

    private static CreateAndPlaceOrderRequest.Order order() {
        return CreateAndPlaceOrderRequest.Order.builder()
                .salt(123456789L)
                .maker(MAKER)
                .signer(SIGNER)
                .tokenId("97633282141727012715463055428185899320650283789416612830799232303088670062150")
                .makerAmount("1500000")
                .takerAmount("5000000")
                .side(TradeSide.BUY)
                .signatureType(2)
                .timestamp("1761840000000")
                .builder("0x00000000000000000000000000000000000000000000000000000000000000ab")
                .build();
    }

    @Test
    void signsStandardOrderLikeReferenceImplementation() {
        String signature = Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, order(), false);

        assertEquals(EXPECTED_STANDARD_SIGNATURE, signature);
    }

    @Test
    void signsNegRiskOrderLikeReferenceImplementation() {
        String signature = Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, order(), true);

        assertEquals(EXPECTED_NEG_RISK_SIGNATURE, signature);
    }

    @Test
    void defaultOverloadSignsForStandardExchange() {
        String signature = Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, order());

        assertEquals(EXPECTED_STANDARD_SIGNATURE, signature);
    }

    @Test
    void expirationIsNotPartOfSignedStruct() {
        CreateAndPlaceOrderRequest.Order order = order();
        order.setExpiration("1900000000");

        String signature = Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, order, false);

        assertEquals(EXPECTED_STANDARD_SIGNATURE, signature);
    }

    @Test
    void timestampIsPartOfSignedStruct() {
        CreateAndPlaceOrderRequest.Order order = order();
        order.setTimestamp("1761840000001");

        String signature = Eip712Signer.signClobPolymarketOrderTypedData(137, PRIVATE_KEY, order, false);

        assertNotEquals(EXPECTED_STANDARD_SIGNATURE, signature);
    }
}
