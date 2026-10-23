package dev.polymarket.client;

import dev.polymarket.client.api.clob.AuthL1Api;
import dev.polymarket.client.api.clob.AuthL2Api;
import dev.polymarket.client.api.clob.OrderApi;
import dev.polymarket.client.api.gamma.MarketsApi;
import dev.polymarket.client.api.interceptor.AuthL1Interceptor;
import dev.polymarket.client.api.interceptor.AuthL2Interceptor;
import dev.polymarket.client.dto.PolymarketContext;
import dev.polymarket.client.dto.api.response.PolymarketCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for interacting with the Polymarket CLOB (Central Limit Order Book) API.
 * <p>
 * Provides access to both authenticated (private) and public APIs for trading on Polymarket.
 */
public class PolymarketClient {
    private static final Logger log = LoggerFactory.getLogger(PolymarketClient.class);

    private final PolymarketContext context;

    // Internal instances backing the lazy double-checked locking properties
    private volatile AuthL1Interceptor authL1Interceptor;
    private volatile AuthL2Interceptor authL2Interceptor;
    private volatile PolymarketCredentials credentials;
    private volatile PrivateApi privateApi;
    private volatile PublicApi publicApi;

    // Primary constructor with a default fallback instance parameter matching Kotlin
    public PolymarketClient(PolymarketContext context) {
        this.context = context != null ? context : new PolymarketContext();
    }

    public PolymarketClient() {
        this(new PolymarketContext());
    }

    // --- Thread-Safe Lazy Getters ---

    private AuthL1Interceptor getAuthL1Interceptor() {
        if (authL1Interceptor == null) {
            synchronized (this) {
                if (authL1Interceptor == null) {
                    authL1Interceptor = context.toAuthL1Interceptor();
                }
            }
        }
        return authL1Interceptor;
    }

    private AuthL2Interceptor getAuthL2Interceptor() {
        if (authL2Interceptor == null) {
            synchronized (this) {
                if (authL2Interceptor == null) {
                    authL2Interceptor = new AuthL2Interceptor(context.getAddress(), getCredentials());
                }
            }
        }
        return authL2Interceptor;
    }

    public PrivateApi getPrivateApi() {
        if (privateApi == null) {
            synchronized (this) {
                if (privateApi == null) {
                    privateApi = new PrivateApi();
                }
            }
        }
        return privateApi;
    }

    public PublicApi getPublicApi() {
        if (publicApi == null) {
            synchronized (this) {
                if (publicApi == null) {
                    publicApi = new PublicApi();
                }
            }
        }
        return publicApi;
    }

    /**
     * API credentials obtained from Polymarket.
     * <p>
     * Lazily initialized by attempting to derive existing API keys,
     * or creating new ones if derivation fails.
     */
    public PolymarketCredentials getCredentials() {
        if (credentials == null) {
            synchronized (this) {
                if (credentials == null) {
                    try {
                        // Attempt to derive the key synchronously via .join()
                        credentials = getPrivateApi().getAuthL1Api().deriveApiKey().join();
                    } catch (Exception e) {
                        log.warn("Failed to derive api key. Try to create api keys...", e);
                        // Fallback step to create a new key instead
                        credentials = getPrivateApi().getAuthL1Api().createApiKey().join();
                    }
                }
            }
        }
        return credentials;
    }

    // ==========================================
    // Inner/Nested API Classes
    // ==========================================

    /**
     * Container for authenticated API endpoints.
     */
    public class PrivateApi {
        private final AuthL1Api authL1Api;
        private volatile AuthL2Api authL2Api;
        private volatile OrderApi orderApi;

        public PrivateApi() {
            this.authL1Api = new AuthL1Api(getAuthL1Interceptor());
        }

        public AuthL1Api getAuthL1Api() {
            return authL1Api;
        }

        public AuthL2Api getAuthL2Api() {
            if (authL2Api == null) {
                synchronized (this) {
                    if (authL2Api == null) {
                        authL2Api = new AuthL2Api(getAuthL2Interceptor());
                    }
                }
            }
            return authL2Api;
        }

        public OrderApi getOrderApi() {
            if (orderApi == null) {
                synchronized (this) {
                    if (orderApi == null) {
                        orderApi = new OrderApi(
                                context,
                                getCredentials().getApiKey(),
                                getAuthL2Interceptor()
                        );
                    }
                }
            }
            return orderApi;
        }
    }

    /**
     * Container for public (unauthenticated) API endpoints.
     */
    public class PublicApi {
        private final MarketsApi marketsApi;

        public PublicApi() {
            this.marketsApi = new MarketsApi();
        }

        public MarketsApi getMarketsApi() {
            return marketsApi;
        }
    }
}
