package dev.polymarket.client.api.interceptor;

import dev.polymarket.client.enums.header.PolymarketL1Header;
import dev.polymarket.client.utils.Eip712Signer;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp interceptor for L1 (wallet-based) authentication with Polymarket API.
 * <p>
 * Adds required authentication headers to each request:
 * - POLY_ADDRESS - Wallet address
 * - POLY_SIGNATURE - EIP-712 signature
 * - POLY_TIMESTAMP - Current Unix timestamp
 * - POLY_NONCE - Nonce value
 */
public class AuthL1Interceptor implements Interceptor {

    private final long chainId;
    private final String address;
    private final String privateKey;
    private final long nonce;

    public AuthL1Interceptor(long chainId, String address, String privateKey, long nonce) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address must not be blank");
        }
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("PrivateKey must not be blank");
        }

        this.chainId = chainId;
        this.address = address;
        this.privateKey = privateKey;
        this.nonce = nonce;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        long timestamp = System.currentTimeMillis() / 1000;

        // Generate signature via the companion utility class translation
        String signature = Eip712Signer.signClobPolymarketAuthTypedData(
                chainId,
                address,
                timestamp,
                privateKey
        );

        Map<PolymarketL1Header, String> valueByHeader = new HashMap<>();
        valueByHeader.put(PolymarketL1Header.POLY_ADDRESS, address);
        valueByHeader.put(PolymarketL1Header.POLY_SIGNATURE, signature);
        valueByHeader.put(PolymarketL1Header.POLY_TIMESTAMP, String.valueOf(timestamp));
        valueByHeader.put(PolymarketL1Header.POLY_NONCE, String.valueOf(nonce));

        Request.Builder requestBuilder = chain.request().newBuilder();

        // Loop through and inject headers dynamically using the enum constant names
        valueByHeader.forEach((header, value) -> requestBuilder.addHeader(header.name(), value));

        return chain.proceed(requestBuilder.build());
    }
}
