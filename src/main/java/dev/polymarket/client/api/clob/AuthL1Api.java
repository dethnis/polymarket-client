package dev.polymarket.client.api.clob;


import dev.polymarket.client.api.AbstractApi;
import dev.polymarket.client.api.interceptor.AuthL1Interceptor;
import dev.polymarket.client.dto.api.response.PolymarketCredentials;
import dev.polymarket.client.enums.system.SystemEnv;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * API client for L1 (wallet-based) authentication with Polymarket CLOB.
 * <p>
 * Provides methods for creating and deriving API key credentials
 * using wallet signature authentication.
 */
public class AuthL1Api extends AbstractApi {

    // Primary Constructor
    public AuthL1Api(AuthL1Interceptor authL1Interceptor, String baseUrl) {
        super(
                baseUrl,
                Collections.singletonList(authL1Interceptor)
        );
    }

    // Constructor with default baseUrl mimicking Kotlin's default argument fallback
    public AuthL1Api(AuthL1Interceptor authL1Interceptor) {
        this(
                authL1Interceptor,
                SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault() + "/auth"
        );
    }

    /**
     * Creates new API key credentials for a user.
     * <p>
     * Generates a new set of credentials (apiKey, secret, passphrase)
     * for the authenticated wallet address.
     *
     * @return CompletableFuture with PolymarketCredentials containing new API credentials.
     */
    public CompletableFuture<PolymarketCredentials> createApiKey() {
        return super.post("/api-key", null, PolymarketCredentials.class);
    }

    /**
     * Derives an existing API key for the authenticated address and nonce.
     * <p>
     * Retrieves previously created credentials without generating new ones.
     * Useful for recovering credentials on a new device.
     *
     * @return CompletableFuture with PolymarketCredentials containing existing API credentials.
     */
    public CompletableFuture<PolymarketCredentials> deriveApiKey() {
        return super.get("/derive-api-key", PolymarketCredentials.class);
    }
}