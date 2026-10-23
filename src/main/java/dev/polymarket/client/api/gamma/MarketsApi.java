package dev.polymarket.client.api.gamma;


import dev.polymarket.client.api.AbstractApi;
import dev.polymarket.client.dto.api.response.gamma.MarketResponse;
import dev.polymarket.client.enums.system.SystemEnv;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * API client for accessing Polymarket Gamma markets data.
 * <p>
 * Provides methods for fetching market information from the Gamma API.
 * This is a public API that does not require authentication.
 */
public class MarketsApi extends AbstractApi {

    // Primary Constructor
    public MarketsApi(String baseUrl) {
        super(baseUrl);
    }

    // Default Constructor mimicking Kotlin's default property initialization
    public MarketsApi() {
        this(SystemEnv.GAMMA_API_POLYMARKET_URL.getEnvOrDefault() + "/markets");
    }

    /**
     * Fetches market data by its slug identifier.
     *
     * @param slug Unique slug identifier of the market (e.g., "what-price-will-bitcoin-hit-in-2025").
     * @return CompletableFuture containing the MarketResponse details.
     */
    public CompletableFuture<MarketResponse> getMarketBySlug(String slug) {
        return super.get("/slug/" + slug, MarketResponse.class);
    }

    /**
     * Fetches market data by filter query params
     *
     * @param queryParams map of parameter to filter markets
     * @return CompletableFuture containing the List of MarketResponse details.
     */
    public CompletableFuture<List<MarketResponse>> getMarketByQueryParam(Map<String, String> queryParams) {
        return super.getQuery("", queryParams, new TypeReference<List<MarketResponse>>() {});
    }

}
