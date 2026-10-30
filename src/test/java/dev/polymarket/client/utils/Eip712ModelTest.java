package dev.polymarket.client.utils;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Eip712ModelTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode typedData(boolean negRisk) {
        Eip712Model.PolymarketClobOrder.OrderMessage message = new Eip712Model.PolymarketClobOrder.OrderMessage(
                "1", "0x0", "0x0", "1", "1", "1", "0", "0", "1",
                Eip712Model.BYTES32_ZERO, Eip712Model.BYTES32_ZERO
        );
        return MAPPER.valueToTree(Eip712Model.PolymarketClobOrder.of(137, negRisk, message));
    }

    @Test
    void orderDomainIsCtfExchangeV2() {
        JsonNode domain = typedData(false).get("domain");

        assertEquals("Polymarket CTF Exchange", domain.get("name").asString());
        assertEquals("2", domain.get("version").asString());
        assertEquals(137, domain.get("chainId").asLong());
    }

    @Test
    void standardMarketUsesV2Exchange() {
        assertEquals("0xE111180000d2663C0091e4f400237545B87B996B",
                typedData(false).get("domain").get("verifyingContract").asString());
    }

    @Test
    void negRiskMarketUsesV2NegRiskExchange() {
        assertEquals("0xe2222d279d744050d28e00520010520000310F59",
                typedData(true).get("domain").get("verifyingContract").asString());
    }

    @Test
    void orderStructMatchesV2ContractFieldOrder() {
        List<String> fields = new ArrayList<>();
        typedData(false).get("types").get("Order")
                .forEach(field -> fields.add(field.get("name").asString() + ":" + field.get("type").asString()));

        assertEquals(List.of(
                "salt:uint256",
                "maker:address",
                "signer:address",
                "tokenId:uint256",
                "makerAmount:uint256",
                "takerAmount:uint256",
                "side:uint8",
                "signatureType:uint8",
                "timestamp:uint256",
                "metadata:bytes32",
                "builder:bytes32"
        ), fields);
    }

    @Test
    void authDomainStaysV1() {
        JsonNode domain = MAPPER.valueToTree(Eip712Model.PolymarketClobAuth.of(137, "0x0", 0)).get("domain");

        assertEquals("ClobAuthDomain", domain.get("name").asString());
        assertEquals("1", domain.get("version").asString());
    }
}
