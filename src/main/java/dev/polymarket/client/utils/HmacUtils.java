package dev.polymarket.client.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class HmacUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private HmacUtils() {}

    /**
     * Overload to support requests without a body (maps to Kotlin's default null parameter).
     */
    public static String getHmacPolymarketSignature(
            String secret,
            long timestamp,
            String method,
            String requestPath
    ) {
        return getHmacPolymarketSignature(secret, timestamp, method, requestPath, null);
    }

    /**
     * Generates an HMAC-SHA256 signature encoded in Base64 URL format for Polymarket authentication.
     */
    public static String getHmacPolymarketSignature(
            String secret,
            long timestamp,
            String method,
            String requestPath,
            String body
    ) {
        String bodyPayload = body != null ? body : "";
        String message = timestamp + method + requestPath + bodyPayload;

        byte[] base64Secret = Base64.getUrlDecoder().decode(secret);
        SecretKeySpec secretKey = new SecretKeySpec(base64Secret, HMAC_ALGORITHM);

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] signatureBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate HMAC signature", e);
        }
    }
}
