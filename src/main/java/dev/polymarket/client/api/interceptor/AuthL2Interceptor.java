package dev.polymarket.client.api.interceptor;

import dev.polymarket.client.dto.api.response.PolymarketCredentials;
import dev.polymarket.client.enums.header.PolymarketL2Header;
import dev.polymarket.client.utils.HmacUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp interceptor for L2 (API key-based) authentication with Polymarket API.
 * <p>
 * Adds required authentication headers to each request:
 * - POLY_ADDRESS - Wallet address
 * - POLY_SIGNATURE - HMAC signature of the request
 * - POLY_TIMESTAMP - Current Unix timestamp
 * - POLY_API_KEY - API key
 * - POLY_PASSPHRASE - API passphrase
 */
public class AuthL2Interceptor implements Interceptor {

    private final String address;
    private final PolymarketCredentials credentials;

    public AuthL2Interceptor(String address, PolymarketCredentials credentials) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address must not be blank");
        }
        this.address = address;
        this.credentials = credentials;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long timestamp = System.currentTimeMillis() / 1000;

        // Replicate requestPath joining logic: /path/to/endpoint
        String requestPath = "/" + String.join("/", request.url().pathSegments());

        // Extract the request body as a String if present
        String bodyString = null;
        RequestBody body = request.body();
        if (body != null) {
            try (Buffer buffer = new Buffer()) {
                body.writeTo(buffer);
                String rawBody = buffer.readUtf8();
                if (!rawBody.trim().isEmpty()) {
                    bodyString = rawBody;
                }
            }
        }

        // Calculate HMAC signature
        String signature = HmacUtils.getHmacPolymarketSignature(
                credentials.getSecret(),
                timestamp,
                request.method(),
                requestPath,
                bodyString
        );

        Map<PolymarketL2Header, String> valueByHeader = new HashMap<>();
        valueByHeader.put(PolymarketL2Header.POLY_ADDRESS, address);
        valueByHeader.put(PolymarketL2Header.POLY_SIGNATURE, signature);
        valueByHeader.put(PolymarketL2Header.POLY_TIMESTAMP, String.valueOf(timestamp));
        valueByHeader.put(PolymarketL2Header.POLY_API_KEY, credentials.getApiKey());
        valueByHeader.put(PolymarketL2Header.POLY_PASSPHRASE, credentials.getPassphrase());

        Request.Builder requestBuilder = request.newBuilder();
        valueByHeader.forEach((header, value) -> requestBuilder.addHeader(header.name(), value));

        return chain.proceed(requestBuilder.build());
    }
}
