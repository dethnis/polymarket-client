package dev.polymarket.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The first vector is taken verbatim from Polymarket's py-clob-client-v2 (tests/signing/test_hmac.py).
 * The others were computed independently with Python's hmac/base64 using the same algorithm.
 */
class HmacUtilsTest {

    private static final String SECRET = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    @Test
    void matchesOfficialClientVector() {
        String signature = HmacUtils.getHmacPolymarketSignature(
                SECRET, 1000000, "test-sign", "/orders", "{\"hash\": \"0x123\"}");

        assertEquals("ZwAdJKvoYRlEKDkNMwd5BuwNNtg93kNaR_oU2HrfVvc=", signature);
    }

    @Test
    void signsRequestWithoutBody() {
        String signature = HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "GET", "/auth/api-keys");

        assertEquals("XNyd_XTX99P8vpXk1sjk2OZ-sbadQePSifg4gurBXRg=", signature);
    }

    @Test
    void signsCompactJsonBody() {
        String signature = HmacUtils.getHmacPolymarketSignature(
                SECRET, 1700000000, "DELETE", "/order", "{\"orderID\":\"0xabc\"}");

        assertEquals("ebd_Mp184tE7Z6KPR1h4np8gSxcgEPZ7MeKtDjVwf2g=", signature);
    }

    @Test
    void encodesBodyAsUtf8() {
        String signature = HmacUtils.getHmacPolymarketSignature(
                SECRET, 1700000000, "POST", "/order", "{\"emoji\":\"😃\"}");

        assertEquals("i25s1w9-mzz1AbbEK4Kf8rVuMN2r5wA4jChPCZGGXJ8=", signature);
    }

    @Test
    void nullAndEmptyBodyAreEquivalent() {
        String noBody = HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "GET", "/auth/api-keys");

        assertEquals(noBody, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "GET", "/auth/api-keys", null));
        assertEquals(noBody, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "GET", "/auth/api-keys", ""));
    }

    @Test
    void everyInputAffectsSignature() {
        String base = HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "POST", "/order", "{}");

        assertNotEquals(base, HmacUtils.getHmacPolymarketSignature(
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=", 1700000000, "POST", "/order", "{}"));
        assertNotEquals(base, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000001, "POST", "/order", "{}"));
        assertNotEquals(base, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "DELETE", "/order", "{}"));
        assertNotEquals(base, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "POST", "/orders", "{}"));
        assertNotEquals(base, HmacUtils.getHmacPolymarketSignature(SECRET, 1700000000, "POST", "/order", "{ }"));
    }

    @Test
    void rejectsSecretThatIsNotBase64() {
        assertThrows(IllegalArgumentException.class,
                () -> HmacUtils.getHmacPolymarketSignature("!!!notbase64!!!", 1700000000, "GET", "/order"));
    }
}
