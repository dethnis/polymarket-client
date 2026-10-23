package dev.polymarket.client.api.clob;

import dev.polymarket.client.api.AbstractApi;
import dev.polymarket.client.api.interceptor.AuthL2Interceptor;
import dev.polymarket.client.dto.api.response.ApiKeysResponse;
import dev.polymarket.client.dto.api.response.ClosedOnlyModeStatusResponse;
import dev.polymarket.client.enums.system.SystemEnv;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * API client for L2 (API key-based) authentication with Polymarket CLOB.
 * <p>
 * L2 authentication uses API key, secret, and passphrase credentials
 * for authenticating requests to Polymarket's CLOB (posting/canceling orders,
 * retrieving account orders and fills).
 */
public class AuthL2Api extends AbstractApi {

    // Primary Constructor
    public AuthL2Api(AuthL2Interceptor authL2Interceptor, String baseUrl) {
        super(
                baseUrl,
                Collections.singletonList(authL2Interceptor)
        );
    }

    // Default Constructor mimicking Kotlin's default argument property fallback
    public AuthL2Api(AuthL2Interceptor authL2Interceptor) {
        this(
                authL2Interceptor,
                SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault() + "/auth"
        );
    }

    /**
     * Retrieves all API keys associated with the authenticated Polygon address.
     *
     * @return CompletableFuture with ApiKeysResponse containing list of API keys.
     */
    public CompletableFuture<ApiKeysResponse> getApiKeys() {
        return super.get("/api-keys", ApiKeysResponse.class);
    }

    /**
     * Deletes the API key used to authenticate this request.
     * <p>
     * After deletion, the credentials can no longer be used for authentication.
     *
     * @return CompletableFuture with the untyped deletion response result.
     */
    public CompletableFuture<Object> deleteApiKey() {
        return super.delete("/api-key", null, Object.class);
    }

    /**
     * Retrieves the closed-only mode status for the authenticated address.
     * <p>
     * Closed-only mode restricts the account to only closing existing positions.
     *
     * @return CompletableFuture with ClosedOnlyModeStatusResponse containing status flag.
     */
    public CompletableFuture<ClosedOnlyModeStatusResponse> getClosedOnlyModeStatus() {
        return super.get("/ban-status/closed-only", ClosedOnlyModeStatusResponse.class);
    }
}
